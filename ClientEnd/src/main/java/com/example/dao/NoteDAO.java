package com.example.dao;

import com.example.pojo.Note;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface NoteDAO {
    // by default, sort by note term start time, then created time
    List<Note> getNotesByFilter(@Param("termId") Integer termId,
                                @Param("courseId") Integer courseId,
                                @Param("start") Date start,
                                @Param("end") String end);

    boolean addNote(@Param("courseId") Integer courseId,
                    @Param("content") String content,
                    @Param("filename") String filename);

    Note getNoteById(@Param("id") Integer id);

    boolean deleteFile(@Param("id") Integer noteId);

    Boolean update(@Param("id") Integer noteId,
                   @Param("cid") Integer courseId,
                   @Param("content") String content,
                   @Param("fn") String filename, Boolean deletable);

    Boolean deleteNote(@Param("id") Integer id);

    Boolean isDeletable(Integer id);
}
