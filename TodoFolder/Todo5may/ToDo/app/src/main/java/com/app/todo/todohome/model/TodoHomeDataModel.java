package com.app.todo.todohome.model;

public class TodoHomeDataModel {
    public boolean isArchieve;
    private int id;
    private String title;
    private String description;
    private String reminderDate;

    private String startdate;

    public TodoHomeDataModel() {


    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public boolean isArchieve() {
        return isArchieve;
    }

    public void setArchieve(boolean archieve) {
        isArchieve = archieve;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }


    }
