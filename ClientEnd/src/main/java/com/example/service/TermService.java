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
    public boolean deleteById(Integer id){
        return termDAO.deleteTermById(id);
    }

    public Term getById(Integer id) {
        return termDAO.getById(id);
    }

    public boolean editTerm(Integer id, String name, Date date) {
        return termDAO.editTerm(id, name, date);
    }
}
