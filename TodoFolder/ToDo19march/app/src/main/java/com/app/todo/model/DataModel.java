package com.app.todo.model;

public class DataModel {
    private String id;
    private String Title;
    private String Description;
    private String Datepicker;
    public DataModel(String Title, String Description,String id) {
        this.Title = Title;
        this.Description = Description;
        this.id=id;
    }
    public DataModel(String s, String toString) {
        this.Title = s;
        this.Description = toString;
    }

    public DataModel() {

    }

    public String getTitle() {

        return Title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {

        return id;
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
