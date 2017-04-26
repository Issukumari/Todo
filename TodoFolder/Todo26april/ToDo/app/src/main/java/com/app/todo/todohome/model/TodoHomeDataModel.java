package com.app.todo.todohome.model;

public class TodoHomeDataModel {
    private int id;
    private String title;
    private String description;

    public TodoHomeDataModel(String Title, String Description, int id) {
        this.title = Title;
        this.description = Description;
        this.id=id;
    }

    public TodoHomeDataModel() {

    }



    public String getTitle() {

        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {

        return id;
    }

    public void setTitle(String Title) {


        this.title = Title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String Description) {
        this.description = Description;
    }

}
