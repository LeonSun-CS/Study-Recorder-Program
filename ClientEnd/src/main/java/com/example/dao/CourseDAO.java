package com.example.dao;

import com.example.pojo.Course;
import com.example.pojo.Term;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseDAO {
    List<Course> getAllCourses(); // by default, sort by term start date and then course name
    List<Course> getCoursesByTerm(int termId); // by default, sort by course name
    boolean addCourse(@Param("name") String name,
                      @Param("desc") String desc,
                      @Param("termId") Integer termId,
                      @Param("inp") Boolean inp,
                      @Param("mr") Boolean mr);

    boolean deleteById(@Param("id") Integer id);

    Course getById(@Param("id") Integer id);

    boolean updateCourse(@Param("id") Integer id,
                         @Param("name") String name,
                         @Param("desc") String desc,
                         @Param("termId") Integer termId,
                         @Param("inp") Boolean inp,
                         @Param("mr") Boolean mr,
                         @Param("deletable") Boolean deletable);

    boolean checkNotesExistForCourse(@Param("course_id") Integer id);
    boolean isDeletable(@Param("id") Integer id);
}
