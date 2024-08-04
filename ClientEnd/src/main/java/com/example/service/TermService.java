package com.example.service;

import com.example.dao.TermDAO;
import com.example.pojo.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TermService {
    @Autowired
    TermDAO termDAO;
    public List<Term> getAllTerms() {
        return termDAO.getAllTerms();
    }
    public boolean addTerm(String name, Date date) {return termDAO.addTerm(name, date);}
    public String deleteById(Integer id){
        // first check preconditions to delete:
        // 1. no course is associated with this term
        if (termDAO.checkCoursesExistForTerm(id)) {
            return "couldn't delete term because there are courses associated with it";
        }
        // 2. the deletable data is set to true
        if (!termDAO.isDeletable(id)) {
            return "couldn't delete term because the term is not deletable";
        }
        termDAO.deleteTermById(id);
        return "success";
    }

    public Term getById(Integer id) {
        return termDAO.getById(id);
    }

    public boolean editTerm(Integer id, String name, Date date, boolean deletable) {
        return termDAO.editTerm(id, name, date, deletable);
    }

}
