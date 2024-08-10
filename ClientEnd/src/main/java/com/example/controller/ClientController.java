package com.example.controller;

import com.example.pojo.*;
import com.example.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

@Controller
public class ClientController {
    @Autowired
    CourseService courseService;
    @Autowired
    TermService termService;
    @Autowired
    NoteService noteService;
    @Autowired
    UserService userService;
    @Autowired
    private Quota quotaObject;
    @Autowired
    private AuthHelper authHelper;
    @Autowired
    JedisPool jedisPool;
    @Autowired
    CaptchaService captchaService;
    @Value("${file.path}")
    private String filePath;

    @RequestMapping("/")
    public ModelAndView indexPage() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");
        List<Course> allCourses = courseService.getAllCourses();
        mv.addObject("courses", allCourses);

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @GetMapping("/captcha")
    public ModelAndView captchaPage(@RequestParam(value = "returnUrl", required = false) String returnUrl) {
        ModelAndView mv = new ModelAndView("captcha");

        // TODO: DEBUG ***************************************************
//        System.out.println("/captcha" + (returnUrl != null && !returnUrl.isEmpty() ? "" : "?returnUrl=" + returnUrl));
        System.out.println("returnUrl = " + returnUrl);
        // TODO: DEBUG ***************************************************

        mv.addObject("returnUrl", returnUrl);
//        mv.addObject("actionUrl", "/captcha" + (returnUrl != null && !returnUrl.isEmpty() ? "?returnUrl=" + returnUrl : ""));
        return mv;
    }

    @PostMapping("/captcha")
    public ModelAndView catchaProcess(@RequestParam(value = "cf-turnstile-response", required = false) String tsResponse,
                                      @RequestParam(value = "returnUrl", required = false) String returnUrl,
                                      HttpServletRequest request
    ) {
        ModelAndView mv = new ModelAndView();
        // before everything: check turnstile
        if (tsResponse == null) {
            mv.addObject("msg", "Missing captcha parameters");
            mv.addObject("quota", quotaObject);
            mv.addObject("auth", authHelper);
            mv.setViewName("fail");
            return mv;
        }
        String verifiedCaptcha = captchaService.verify(tsResponse);
        if (!"success".equals(verifiedCaptcha)) {
            mv.addObject("quota", quotaObject);
            mv.addObject("auth", authHelper);
            mv.addObject("msg", "failed captcha: " + verifiedCaptcha + "\n\t" +
                    "Please avoid using refresh button on the <b>NOTES</b> page. ");
            mv.setViewName("fail");
            return mv;
        }

        // add a record in redis for later verification, because we only need to
        // verify once for each session or every 12 hours (whichever comes first)
        try (Jedis jedis = jedisPool.getResource()) {
            authHelper.setSessionId(request.getSession().getId());
            jedis.set("captcha-" + authHelper.getSessionId(), "yes", SetParams.setParams().ex(43200));
        }

        // here we have verified the status of the captcha
        // if the redirectUrl is not null, then redirect to index
        if (returnUrl == null || returnUrl.isEmpty()) {
            mv.setViewName("redirect:/");
            return mv;
        }
        mv.setViewName("redirect:" + returnUrl);
        return mv;
    }

    @RequestMapping(value = "/notes", method = RequestMethod.GET)
    public ModelAndView notes(@RequestParam(value = "term", required = false, defaultValue = "0") Integer termId,
                              @RequestParam(value = "course", required = false, defaultValue = "0") Integer courseId,
                              @RequestParam(value = "start", required = false) String start,
                              @RequestParam(value = "end", required = false) String end) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);

        Date startDate;
        Date endDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        // since the start param cannot be parsed as Date in the method signature, we need to parse it manually
        try {
            if (start != null && !start.isEmpty()) {
                startDate = format.parse(start);
            } else {
                // initialize startDate as 120 days before the current date
                Instant instant = LocalDate.now().minusDays(120).atStartOfDay(ZoneId.systemDefault()).toInstant();
                startDate = Date.from(instant);
            }
            if (end != null && !end.isEmpty()) {
                endDate = format.parse(end);
            } else {
                // if the end date is not provided, set it to the current date
                endDate = new Date();
            }
        } catch (ParseException e) {
            mv.addObject("msg", "Invalid date format. Please use yyyy-MM-dd format.");
            mv.setViewName("fail");
            return mv;
        }
        List<Term> terms = termService.getAllTerms();
        mv.addObject("terms", terms);
        List<Course> allCourses = courseService.getAllCourses();
        mv.addObject("courses", allCourses);
        List<Note> notes = noteService.getNotesByFilter(termId, courseId, startDate, endDate);
        mv.addObject("notes", notes);

        mv.addObject("auth", authHelper);

        mv.addObject("placeholderStart", format.format(startDate));
        mv.addObject("placeholderEnd", format.format(endDate));
        mv.addObject("placeholderTermId", termId);
        mv.addObject("placeholderCourseId", courseId);
        mv.setViewName("notes");
        return mv;
    }

    /**
     * Because this method is not passing through any of interceptors, it doesn't have quotaObject which is
     * a Request domain bean, or maybe an authHelper object which is a Session domain bean.
     *
     * @return
     */
    @GetMapping("/limit_reached")
    public ModelAndView rejectAccess(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("limit_reached");
        if (quotaObject.getIp() == null) {
            quotaObject.setIp(request.getHeader("X-Real-IP"));
//            quotaObject.setIp(request.getRemoteAddr());
        }
        try (Jedis jedis = jedisPool.getResource()) {

            if (jedis.get("ip-" + quotaObject.getIp()) == null) {
                /*mv.setViewName("redirect:/");
                return mv;*/
                quotaObject.setRemaining(50);
            } else {
                quotaObject.setRemaining(Integer.valueOf(jedis.get("ip-" + quotaObject.getIp())));
            }
            mv.addObject("quota", quotaObject);

            authHelper.setAuthed("yes".equals(jedis.get("sid-" + authHelper.getSessionId())));
            mv.addObject("auth", authHelper);
            return mv;
        }
    }

    @GetMapping("/auth")
    public ModelAndView authPage(HttpServletRequest request,
                                 @RequestParam(value = "returnUrl", required = false) String returnUrl
    ) {
        ModelAndView mv = new ModelAndView("auth");

        // check if already verified
        String id = request.getSession().getId();

        // set return URL; if the return URL is present as parameter, use it;
        // otherwise, use the referer
        if (returnUrl == null || returnUrl.isEmpty()) {
            returnUrl = request.getHeader("Referer");
            try {
                // try to clean the return url from the referer
                returnUrl = new URL(returnUrl).getPath();
                // TODO: DEBUG***************************************************
                System.out.println("Referer URL: " + returnUrl);
            } catch (MalformedURLException e) {
                // TODO: DEBUG***************************************************
                System.out.println("Malformed URL: " + returnUrl);
                // prevent later usage of the variable returnUrl
                returnUrl = null;
            }
        }
        try (Jedis jedis = jedisPool.getResource()) {

            String s = jedis.get("sid-" + id);
            if ("yes".equals(s)) {
                // already verified identity
                mv.setViewName("redirect:/" + (returnUrl != null && !returnUrl.isEmpty() ? returnUrl : ""));
                return mv;
            }

            mv.addObject("quota", quotaObject);
            mv.addObject("auth", authHelper);
            if ("/captcha".equals(returnUrl)) {
                returnUrl = null;
            }
            mv.addObject("returnUrl", returnUrl);
            return mv;
        }
    }

    @PostMapping("/auth")
    public ModelAndView authProcess(@RequestParam("username") String un,
                                    @RequestParam("password") String pw,
                                    HttpServletRequest request,
                                    @RequestParam(value = "cf-turnstile-response", required = false) String tsResponse,
                                    @RequestParam(value = "returnUrl", required = false) String returnUrl
    ) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);

        // before everything: check turnstile
        if (tsResponse == null) {
            mv.addObject("msg", "Missing captcha parameters");
            mv.setViewName("fail");
            return mv;
        }
        String verifiedCaptcha = captchaService.verify(tsResponse);
        if (!"success".equals(verifiedCaptcha)) {
            mv.addObject("msg", "failed captcha: " + verifiedCaptcha);
            mv.setViewName("fail");
            return mv;
        }

        // check if already verified
        String id = request.getSession().getId();

        try (Jedis jedis = jedisPool.getResource()) {

            String s = jedis.get("sid-" + id);
            if ("yes".equals(s)) {
                // already verified identity
                mv.setViewName("redirect:/" + (returnUrl != null && !returnUrl.isEmpty() ? returnUrl : ""));
                return mv;
            }
            // verify username and password match
            if (userService.verifyUser(un, pw)) {
                jedis.set("sid-" + id, "yes", SetParams.setParams().ex(10800));
            } else {
                mv.addObject("msg", "Please check your authentication details");
                mv.setViewName("fail");

                return mv;
            }
        }
        if (returnUrl == null || returnUrl.isEmpty()) {
            mv.setViewName("redirect:/");
            return mv;
        }
        mv.setViewName("redirect:" + returnUrl);
        return mv;
    }

/*    @RequestMapping("/favicon.ico")
    public ResponseEntity<byte[]> getLogo(HttpServletRequest request) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        InputStream in = request.getSession().getServletContext().getResourceAsStream("/files/favicon.ico");
        byte[] media = IOUtils.toByteArray(in);
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping("/favicon.svg")
    public ResponseEntity<byte[]> getLogoForSafariTabs(HttpServletRequest request) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        InputStream in = request.getSession().getServletContext().getResourceAsStream("/files/favicon.svg");
        byte[] media = IOUtils.toByteArray(in);
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
        return responseEntity;
    }*/

    @RequestMapping("/favicon.ico")
    public ResponseEntity<byte[]> getLogo(HttpServletRequest request) {
        return getFileHelper(filePath + "favicon.ico", request);
    }

    @RequestMapping("/favicon.svg")
    public  ResponseEntity<byte[]> getLogoForSafariTabs(HttpServletRequest request) {
        return getFileHelper(filePath + "favicon.svg", request);
    }


    @GetMapping("/logout")
    public ModelAndView logout(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del("sid-" + request.getSession().getId());
        }
        mv.setViewName("redirect:/");
        return mv;
    }

    @GetMapping("/courses-in-term/{termId}")
    public ResponseEntity<String> getCoursesInTerm(@PathVariable("termId") Integer termId) {
        List<Course> courses = courseService.getCoursesByTermId(termId);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();
        // 1. convert object to JSON string
        ArrayNode courseJSONArray = rootNode.putArray("courses");
        courses.forEach(new Consumer<Course>() {
            @Override
            public void accept(Course course) {
                ObjectNode courseJSONObject = courseJSONArray.addObject();
                courseJSONObject.put("id", course.getId());
                courseJSONObject.put("name", course.getName());
            }
        });
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//        format.parse()
        // now add the start date of the term. If the termId is not 0, we can get the start date of that
        // term; if not or any exception is thrown, we can get 120 days before the current date
        String dateString;
        if (termId != 0) {
            try {
                dateString = termService.getById(termId).getStartDate()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                dateString = LocalDate.now().minusDays(120).format(DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } else {
            dateString = LocalDate.now().minusDays(120).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        rootNode.put("start-date", dateString);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(rootNode.toString());
    }

    /**
     * This method is used to get the file under the path "filePath + courseId + / + fileName"
     * from the server and send it to the client.
     */
    @GetMapping("/files/{classId}/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable("classId") Integer classId,
                        @PathVariable("fileName") String fileName,
                        HttpServletRequest request) {
        return getFileHelper(filePath + classId + "/" + fileName, request);
    }

    private ResponseEntity<byte[]> getFileHelper(@PathVariable String fileFullPath, HttpServletRequest request) {
        File file = new File(fileFullPath);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        byte[] fileContent = new byte[0];
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        MediaType mediaType = MediaTypeFactory.getMediaType(file.getName()).orElse(MediaType.APPLICATION_OCTET_STREAM);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentLength(file.length());
        // Add cache control headers if appropriate
        headers.setCacheControl("max-age=1800, must-revalidate");

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }

    @GetMapping("/note/{noteId}")
    public ModelAndView noteDetailPage(@PathVariable("noteId") Integer noteId) {
        ModelAndView mv = new ModelAndView();
        Note note = noteService.getNoteById(noteId);
        if (note == null) {
            mv.addObject("msg", "Note not found");
            mv.setViewName("fail");
            return mv;
        }
        mv.addObject("note", note);
        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        mv.setViewName("note_detail");
        return mv;
    }


}
