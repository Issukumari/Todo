package com.app.todo.todohome.model;

public class TodoHomeDataModel {
    private int id;
    private String title;
    private String description;
    private String reminderdate;


    public String getReminderdate() {
        return reminderdate;
    }

    public void setReminderdate(String reminderdate) {
        this.reminderdate = reminderdate;
    }

    /*public TodoHomeDataModel(String Title, String Description, String reminderdate, int id) {
        this.title = Title;
        this.description = Description;
        this.id = id;
        this.reminderdate = reminderdate;

    }*/

    public TodoHomeDataModel() {


    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String Title) {


        this.title = Title;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String Description) {
        this.description = Description;
    }

}
