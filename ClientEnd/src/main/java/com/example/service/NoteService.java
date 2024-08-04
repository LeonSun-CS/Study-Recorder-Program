package com.example.service;

import com.example.dao.NoteDAO;
import com.example.pojo.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Date;
import java.util.List;

@Service
public class NoteService {
    @Autowired
    NoteDAO noteDAO;
    @Autowired
    ServletContext servletContext;

    public List<Note> getNotesByFilter(Integer termId, Integer courseId, Date start, Date end) {
        termId = (termId == 0) ? null : termId;
        courseId = (courseId == 0) ? null : courseId;
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

    public Boolean update(Integer noteId, Integer courseId, String content, String filename, Boolean deletable) {
        return noteDAO.update(noteId, courseId, content, filename, deletable);
    }

    public String deleteNote(Integer id) {
        Note thisNote = getNoteById(id);
        // check preconditions for deletion: deletable set to true
        if (!thisNote.isDeletable()) {
            return "Note is not deletable!";
        }

        // get the current note to get its associated file path, if any
        if (thisNote.getFileName() != null && !thisNote.getFileName().isEmpty()) {
            // if there is a file associated with this note, delete it
            String realPath = servletContext.getRealPath("/files/" + thisNote.getCourse().getId() + "/" + thisNote.getFileName());
            File file = new File(realPath);
            if (!file.delete()) {
                return "File deletion failed!";
            }
        }
        noteDAO.deleteNote(id);
        return "success";
    }
}
