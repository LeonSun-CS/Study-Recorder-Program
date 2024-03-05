package com.example.controller;

import com.example.pojo.*;
import com.example.service.CourseService;
import com.example.service.NoteService;
import com.example.service.TermService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    Jedis jedis;

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
    @RequestMapping(value = "/notes", method = RequestMethod.GET)
    public ModelAndView notes(@RequestParam(value = "term", required = false) Integer termId,
                              @RequestParam(value = "course", required = false) Integer courseId,
                              @RequestParam(value = "start", required = false)String start,
                              @RequestParam(value = "end", required = false) String end) throws ParseException {
/*        boolean filtered = false;  // the flag to mark if a filter is used
        // filtered is true if any filter is invoked
        if (termId != null || courseId != null || (start != null && !start.isEmpty()) ||
                (end != null && !end.isEmpty())
        ) filtered = true;
        */
        Date startDate = null;
        Date endDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        // since the start param cannot be parsed as Date in the method signature, we need to parse it manually
        if (start != null && !start.isEmpty()) {
            startDate = format.parse(start);
        }
        if (end != null && !end.isEmpty()) {
            endDate = format.parse(end);
        }
        ModelAndView mv = new ModelAndView();
        List<Term> terms = termService.getAllTerms();
        mv.addObject("terms", terms);
        List<Course> allCourses = courseService.getAllCourses();
        mv.addObject("courses", allCourses);
        List<Note> notes = noteService.getNotesByFilter(termId, courseId, startDate, endDate);
        mv.addObject("notes", notes);

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        mv.setViewName("notes");
        return mv;
    }

    /**
     * Because this method is not passing through any of interceptors, it doesn't have quotaObject which is
     * a Request domain bean, or maybe an authHelper object which is a Session domain bean.
     * @return
     */
    @GetMapping("/limit_reached")
    public ModelAndView rejectAccess(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("limit_reached");
        if (quotaObject.getIp() == null) {
            quotaObject.setIp(request.getHeader("X-Real-IP"));
//            quotaObject.setIp(request.getRemoteAddr());
        }
        quotaObject.setRemaining(Integer.valueOf(jedis.get("ip-" + quotaObject.getIp())));
        mv.addObject("quota", quotaObject);

        if (authHelper.getSessionId() == null) {
            authHelper.setSessionId(request.getSession().getId());
            authHelper.setAuthed("yes".equals(jedis.get("sid-" + authHelper.getSessionId())));
        }
        mv.addObject("auth", authHelper);
        return mv;
    }

    @GetMapping("/auth")
    public ModelAndView authPage(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("auth");

        // check if already verified
        String id = request.getSession().getId();
        String s = jedis.get("sid-" + id);
        if ("yes".equals(s)) {
            // already verified identity
            mv.setViewName("redirect:/");
            return mv;
        }

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }
    @PostMapping("/auth")
    public ModelAndView authProcess(@RequestParam("username") String un,
                                    @RequestParam("password") String pw,
                                    HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();

        // check if already verified
        String id = request.getSession().getId();
        String s = jedis.get("sid-" + id);
        if ("yes".equals(s)) {
            // already verified identity
            mv.setViewName("redirect:/");
            return mv;
        }
        // verify username and password match
        if (userService.verifyUser(un, pw)) {
            jedis.set("sid-"+ id, "yes", SetParams.setParams().ex(10800));
        } else {
            mv.addObject("msg", "Please check your authentication details");
            mv.setViewName("fail");

            mv.addObject("quota", quotaObject);
            mv.addObject("auth", authHelper);
            return mv;
        }
        mv.setViewName("redirect:/");
        return mv;
    }
}
