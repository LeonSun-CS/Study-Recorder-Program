package com.example.dao;

import com.example.pojo.Term;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TermDAO {
    List<Term> getAllTerms();  // by default, sort by term start date
    boolean addTerm(@Param("name") String name, @Param("date") Date date);
    boolean deleteTermById(@Param("id") Integer id);

    Term getById(@Param("id") Integer id);

    boolean editTerm(@Param("id") Integer id,
                     @Param("name") String name,
                     @Param("startDate") Date startDate,
                     @Param("deletable") boolean deletable);

    // TODO: TO be completed
    boolean setDeletable(@Param("id") Integer id, @Param("deletable") boolean deletable);
    boolean isDeletable(@Param("id") Integer id);
    boolean checkCoursesExistForTerm(@Param("id") Integer id);
}
