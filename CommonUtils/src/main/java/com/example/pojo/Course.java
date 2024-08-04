package com.example.pojo;

public class Course {
    private int id;
    private String name;
    private String description;
    private Term term;
    private boolean deletable;

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    private boolean inProgress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public boolean isMajorRelated() {
        return majorRelated;
    }

    public void setMajorRelated(boolean majorRelated) {
        this.majorRelated = majorRelated;
    }

    private boolean majorRelated; // this is to mark if this course is required by the department of my declared major
}
