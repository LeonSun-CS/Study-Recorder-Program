package com.example.controller;

import com.example.pojo.*;
import com.example.service.CourseService;
import com.example.service.NoteService;
import com.example.service.TermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Controller
public class AdminController {

    @Autowired
    TermService termService;
    @Autowired
    CourseService courseService;
    @Autowired
    NoteService noteService;
    @Autowired
    private Quota quotaObject;
    @Autowired
    private AuthHelper authHelper;

    @RequestMapping(value = "/term", method = RequestMethod.GET)
    public ModelAndView termPage() {
        ModelAndView mv = new ModelAndView();
        List<Term> terms = termService.getAllTerms();
        mv.addObject("terms", terms);
        mv.setViewName("termlist");

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping(value = "/course", method = RequestMethod.GET)
    public ModelAndView coursePage() {
        ModelAndView mv = new ModelAndView();
        List<Course> course = courseService.getAllCourses();
        mv.addObject("courses", course);
        mv.setViewName("courselist");


        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping(value = "/term", method = RequestMethod.POST)
    public ModelAndView termAddProcess(String name, String date) throws ParseException {
        ModelAndView mv = new ModelAndView();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date parseddate = null;
        if (date != null && !date.isEmpty()) {
            parseddate = format.parse(date);
        }
        boolean b = termService.addTerm(name, parseddate);
        if (b) {
            mv.setViewName("redirect:/term");
        } else {
            mv.setViewName("fail");
            mv.addObject("msg", "Failed to add the term, " + name + ", into Terms!");
        }

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping(value = "/term", method = RequestMethod.PUT)
    public ModelAndView termEditProcess(Integer id, String name, String date) throws ParseException {
        ModelAndView mv = new ModelAndView();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date parseddate = null;
        if (date != null && !date.isEmpty()) {
            parseddate = format.parse(date);
        }
        boolean b = termService.editTerm(id, name, parseddate);
        if (b) {
            mv.setViewName("redirect:/term");
        } else {
            mv.setViewName("fail");
            mv.addObject("msg", "Failed to edit the term, " + name + ", on Terms!");
        }
        return mv;
    }

    @RequestMapping(value = "/course", method = RequestMethod.POST)
    public ModelAndView courseAddProcess(String name, String desc, @RequestParam("term") Integer termId,
                                         Boolean inp, Boolean mr) {
        ModelAndView mv = new ModelAndView();
        boolean b = courseService.addCourse(name, desc, termId, inp, mr);
        if (b) {
            mv.setViewName("redirect:/course");
        } else {
            mv.setViewName("fail");
            mv.addObject("msg", "Failed to add the course, " + name + ", into Courses!");
        }

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping(value = "/term", method = RequestMethod.DELETE)
    public ModelAndView termDelProcess(@RequestParam("id") String did) {
        ModelAndView mv = new ModelAndView();
        boolean b = termService.deleteById(Integer.parseInt(did));
        if (b) {
            mv.setViewName("redirect:/term");
        } else {
            mv.setViewName("fail");
            mv.addObject("msg", "Failed to delete the term from database!");
        }

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping(value = "/course", method = RequestMethod.DELETE)
    public ModelAndView courseDelProcess(@RequestParam("id") String did) {
        ModelAndView mv = new ModelAndView();
        boolean b = courseService.deleteById(Integer.parseInt(did));
        if (b) {
            mv.setViewName("redirect:/course");
        } else {
            mv.setViewName("fail");
            mv.addObject("msg", "Failed to delete the term from database!");
        }

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping("/term_edit/{id}")
    public ModelAndView termEditPage(@PathVariable Integer id) {
        ModelAndView mv = new ModelAndView();
        Term term = termService.getById(id);
        mv.addObject("term", term);
        mv.setViewName("term_edit");


        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping("/course_edit/{id}")
    public ModelAndView courseEditPage(@PathVariable Integer id) {
        ModelAndView mv = new ModelAndView();
        Course course = courseService.getById(id);
        mv.addObject("course", course);
        List<Term> terms = termService.getAllTerms();
        mv.addObject("terms", terms);
        mv.setViewName("course_edit");


        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping(value = "/course", method = RequestMethod.PUT)
    public ModelAndView courseEditProcess(Integer id, String name, String desc, @RequestParam("term") Integer termId,
                                          Boolean inp, Boolean mr) {
        ModelAndView mv = new ModelAndView();
        boolean b = courseService.updateCourse(id, name, desc, termId, inp, mr);
        if (b) {
            mv.setViewName("redirect:/course");
        } else {
            mv.setViewName("fail");
            mv.addObject("msg", "Failed to modify the course, " + name + ", into Courses!");
        }

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping(value = "/term/addition")
    public ModelAndView termAddPage() {
        ModelAndView mv = new ModelAndView("term_addition");

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping("/course/addition")
    public ModelAndView courseAddPage() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("course_addition");
        mv.addObject("terms", termService.getAllTerms());

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping(value = "/notes_admin", method = RequestMethod.GET)
    public ModelAndView noteManagePage(@RequestParam(value = "term", required = false) Integer termId,
                                       @RequestParam(value = "course", required = false) Integer courseId,
                                       @RequestParam(value = "start", required = false)String start,
                                       @RequestParam(value = "end", required = false) String end) throws ParseException {
        ModelAndView mv = new ModelAndView("notes_admin");
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
        List<Term> terms = termService.getAllTerms();
        mv.addObject("terms", terms);
        List<Course> allCourses = courseService.getAllCourses();
        mv.addObject("courses", allCourses);
        List<Note> notes = noteService.getNotesByFilter(termId, courseId, startDate, endDate);
        mv.addObject("notes", notes);

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping("/notes_admin/add")
    public ModelAndView addNotePage() {
        ModelAndView mv = new ModelAndView("note_addition");
        mv.addObject("courses", courseService.getAllCourses());
//        mv.addObject("terms", termService.getAllTerms());

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping("/notes_admin/edit/{id}")
    public ModelAndView editNotePage(@PathVariable Integer id) {
        ModelAndView mv = new ModelAndView("note_edit");
        mv.addObject("courses", courseService.getAllCourses());
        mv.addObject("note", noteService.getNoteById(id));
//        mv.addObject("terms", termService.getAllTerms());

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping(value = "/notes_admin", method = {RequestMethod.POST})
    public ModelAndView noteAddProcess(@RequestParam("cid") Integer courseId,
                                       @RequestParam("content") String content,
                                       @RequestParam(value = "file", required = false) MultipartFile multipartFile,
                                       HttpServletRequest request
                                       ) throws IOException {
        ModelAndView mv = new ModelAndView();

        // *******************************************************************************
        // ******Because I can't send put request directly with a multipart form**********
        // I added multipartFilter to web.xml && multipart-config tag inside the dispatcherServlet
        // *******************************************************************************

        String filename = null; // this store the file name that's to be stored in the database
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String realPath = request.getSession().getServletContext().getRealPath("/files/" + courseId + "/");
            // files are stored under different folders by classes
            String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            String originalFilename = multipartFile.getOriginalFilename();
            if (originalFilename !=null && !originalFilename.isEmpty()) {
                String[] parts = originalFilename.split("\\."); // try to get the file extension
                filename = realPath + uuid + "." + parts[parts.length - 1];
                File file = new File(filename);
                file.getParentFile().mkdirs();
                while (!file.createNewFile()) {
                    // create the file if it doesn't exist; if the file does exist,
                    // try to create one with a new file name to avoid overwriting a file
                    uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
                    filename = realPath + uuid + "." + parts[parts.length - 1];
                }
                multipartFile.transferTo(file);
                // because the current variable "filename" contains the whole absolute
                // path of the file, but we only need the last portion as the real file name
                String[] pathparts = filename.split("/");  // current variable "filename" contains the whole absolute
                // path of the file, but we only need the last portion as the real file name
                filename = pathparts[pathparts.length - 1];
            }
        }

        boolean b = noteService.addNote(courseId, content, filename);
        if (b) {
            mv.setViewName("redirect:/notes_admin");
        } else {
            mv.setViewName("fail");
            mv.addObject("msg", "Failed to add the note into Notes!");
        }

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping(value = "/notes_admin", method = {RequestMethod.PUT})
    public ModelAndView noteEditProcess(@RequestParam("noteid") Integer noteId,
                                        @RequestParam("cid") Integer courseId,
                                        @RequestParam("content") String content,
                                        @RequestParam(value = "file", required = false) MultipartFile multipartFile,
                                        HttpServletRequest request) throws IOException {

        // ********************************IMPORTANT**************************************
        // ******Because I can't send put request directly with a multipart form**********
        // I added multipartFilter to web.xml && multipart-config tag inside the dispatcherServlet
        // *******************************************************************************
        ModelAndView mv = new ModelAndView();
        Note thisNote = noteService.getNoteById(noteId);
        String filename = null;  // the filename to be updated to the database
        if (!multipartFile.isEmpty()) {
            // if a file is uploaded, delete the previous file if exists AND then add the new file to database
            if (thisNote.getFileName() != null && !thisNote.getFileName().isEmpty()) {
                // delete previous file
                String realPath = request.getSession().getServletContext().getRealPath("/files/" + thisNote.getCourse().getId() + "/" + thisNote.getFileName());
                File file = new File(realPath);
                file.delete();
                noteService.deleteFile(thisNote.getId());
            }
            // now add the new file
            String realPath = request.getSession().getServletContext().getRealPath("/files/" + courseId + "/");
            String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
            String originalFilename = multipartFile.getOriginalFilename();
            if (originalFilename !=null && !originalFilename.isEmpty()) {
                String[] parts = originalFilename.split("\\."); // try to get the file extension
                filename = realPath + uuid + "." + parts[parts.length - 1];
                File file = new File(filename);
                file.getParentFile().mkdirs();
                while (!file.createNewFile()) {
                    // create the file if it doesn't exist; if the file does exist,
                    // try to create one with a new file name to avoid overwriting a file
                    uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
                    filename = realPath + uuid + "." + parts[parts.length - 1];
                }
                multipartFile.transferTo(file);
                String[] pathparts = filename.split("/");  // current variable "filename" contains the whole absolute
                // path of the file, but we only need the last portion as the real file name
                filename = pathparts[pathparts.length - 1];
            }
        }
        Boolean b = noteService.update(noteId, courseId, content, filename);
        if (b) {
            mv.setViewName("redirect:/notes_admin");
        } else {
            mv.setViewName("fail");
            mv.addObject("msg", "Failed to update the note!");
        }

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping(value = "/file", method = RequestMethod.DELETE)
    public ModelAndView fileDelete(@RequestParam("fName") String path,
                                   @RequestParam("forNote") Integer noteId,
                                   HttpServletRequest request){
        ModelAndView mv = new ModelAndView();
        String realPath = request.getSession().getServletContext().getRealPath("/files/" + path);  // get the absolute path of the file path
        File file = new File(realPath);
        boolean delete = file.delete();
        if (noteService.deleteFile(noteId)) {
            mv.setViewName("redirect:/notes_admin");
        } else {
            mv.setViewName("fail");
            mv.addObject("msg", "Failed to remove the file from the Notes table!");
        }

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

    @RequestMapping(value = "/notes_admin", method = RequestMethod.DELETE)
    public ModelAndView noteDelProcess(@RequestParam("id") Integer id, HttpServletRequest request){
        ModelAndView mv = new ModelAndView();
        Note thisNote = noteService.getNoteById(id);  // get the current note to get its associated file path
        if (thisNote.getFileName() != null && !thisNote.getFileName().isEmpty()) {
            // if there is a file associated with this note, delete it
            String realPath = request.getSession().getServletContext().getRealPath("/files/" + thisNote.getCourse().getId() + "/" + thisNote.getFileName());
            File file = new File(realPath);
            if (!file.delete()) {
                throw new RuntimeException("File deletion failed!");
            }
        }
        Boolean b = noteService.deleteNote(id);
        if (b) {
            mv.setViewName("redirect:/notes_admin");
        } else {
            mv.setViewName("fail");
            mv.addObject("msg", "Failed to remove the note!");
        }

        mv.addObject("quota", quotaObject);
        mv.addObject("auth", authHelper);
        return mv;
    }

}
