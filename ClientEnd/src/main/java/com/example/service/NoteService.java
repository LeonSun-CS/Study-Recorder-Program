package com.example.service;

import com.example.dao.NoteDAO;
import com.example.pojo.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NoteService {
    @Autowired
    NoteDAO noteDAO;
    public List<Note> getNotesByFilter(Integer termId, Integer courseId, Date start, Date end) {
        return noteDAO.getNotesByFilter(termId, courseId, start, end);
    }

    public boolean addNote(Integer courseId, String content, String filename) {
        return noteDAO.addNote(courseId, content, filename);
    }

    public Note getNoteById(Integer id) {
        return noteDAO.getNoteById(id);
    }

    public boolean deleteFile(Integer noteId) {
        return noteDAO.deleteFile(noteId);
    }

    public Boolean update(Integer noteId, Integer courseId, String content, String filename) {
        return noteDAO.update(noteId, courseId, content, filename);
    }

    public Boolean deleteNote(Integer id) {
        return noteDAO.deleteNote(id);
    }
}
