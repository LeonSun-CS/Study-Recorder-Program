package com.example.service;

import com.example.dao.CourseDAO;
import com.example.pojo.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    CourseDAO courseDao;

    public List<Course> getAllCourses() {
        return courseDao.getAllCourses();
    }

    public boolean addCourse(String name, String desc, Integer termId,
                             Boolean inp, Boolean mr) {
        return courseDao.addCourse(name, desc, termId, inp, mr);
    }

    public String deleteById(Integer id) {
        // first check preconditions to delete:
        // 1. no note is associated with this course
        // 2. the course is deletable
        if (courseDao.checkNotesExistForCourse(id) ){
            return "couldn't delete course because there are notes associated with it";
        }
        if (!courseDao.isDeletable(id)) {
            return "couldn't delete course because the course is not deletable";
        }
        courseDao.deleteById(id);
        return "success";
    }

    public Course getById(Integer id) {
        return courseDao.getById(id);
    }

    public boolean updateCourse(Integer id, String name, String desc, Integer termId, Boolean inp, Boolean mr, Boolean deletable) {
        return courseDao.updateCourse(id, name, desc, termId, inp, mr, deletable);
    }

    public List<Course> getCoursesByTermId(Integer termId) {
        if (termId == null || termId == 0) {
            return courseDao.getAllCourses();
        }
        return courseDao.getCoursesByTerm(termId);
    }
}
