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

    public boolean deleteById(Integer id) {
        return courseDao.deleteById(id);
    }

    public Course getById(Integer id) {
        return courseDao.getById(id);
    }

    public boolean updateCourse(Integer id, String name, String desc, Integer termId, Boolean inp, Boolean mr) {
        return courseDao.updateCourse(id, name, desc, termId, inp, mr);
    }
}
