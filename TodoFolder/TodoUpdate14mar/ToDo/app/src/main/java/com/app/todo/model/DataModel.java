package com.app.todo.model;

public class DataModel {
    private String Title;
    private String Description;
    private String Datepicker;

    public DataModel(String Title, String Description) {
        this.Title = Title;
        this.Description = Description;

    }

    public DataModel() {

    }

public DataModel(String datepicker){
    this.Datepicker=datepicker;

}
    public String getDatepicker()
    {
        return Datepicker;
    }
    public void setDatepicker(String Datepicker) {
        this.Datepicker = Datepicker;
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
