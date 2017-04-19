package com.app.todo.model;

public class DataModel {
    private String id;
    private String title;
    private String description;
    private String datepicker;
    public DataModel(String Title, String Description,String id) {
        this.title = Title;
        this.description = Description;
        this.id=id;
    }
    public DataModel(String s, String toString) {
        this.title = s;
        this.description = toString;
    }

    public DataModel() {

    }

    public String getTitle() {

        return title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {

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
