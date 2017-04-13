package com.app.todo.model;

public class DataModel {
    private String Title;
    private String Description;

    public DataModel(String Title, String Description) {
        this.Title = Title;
        this.Description = Description;

    }

    public DataModel() {

    }


    public String getTitle() {

        return Title;
    }

    public void setTitle(String Title) {

        this.Title = Title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

}
