package com.app.todo.utils;

/**
 * Created by bridgeit on 26/3/17.
 */
public class Constants {
    public static int Timeout = 500;
    public static String keys = "smita";
    public static String values = "";
    public static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static String Password_Pattern="^(?=.*[a-z])(?=.*[0-9]).{5,12}$";
    public static int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "TodoHome";
    public static String TABLE_Addnote = " Addnotes";
    public static String Description = " Description ";
    public static String Title = "Title ";
    public static String id = " id ";
    public static String setTitle = "connection is loose";
    public static String setMessage = " May be your internet connection is off. Please turn it on " + " and try again ";
    public static String Email = "Email";
    public static String password = "password";
    public static String Name = "Name";
    public static String Mobile = "Mobile";
    public static String address = "address";
    public static String Titletext = "Titletext";
    public static String Desriptiontext = "Desriptiontext";
    public static String CREATE_TABLE = "CREATE TABLE ";
    public static String text = " text,";
    public static String DROP_TABLE_IF_EXISTS = " DROP TABLE IF EXISTS";
    public static String textlast=" text";
    public static String fbloginkeys="fblogin";
    public static String googleloginkeys="googlelogin";
    public static String Pic=" Pic";
    public static String email=" email";
/*
    public static String Adjust_code_style_settings=" Adjust code style settings";
*/
    public static String item_Selectd ="item_Selectd";
    public static String please_wait=" please wait....";
    public static String reminderKey="reminder";
    public static String currentdate="currentdate";
    public static String key_firebase_todo="key_firebase_todo";
    public static String note_details = "note_details";
    public static String loginsuccessfull ="loginsuccessfull";
    public static String entercorrectdetails="entercorrectdetails";
    public static String cancel="cancel";
    public static String first_name="first_name";
    public static String last_name="last_name";
    public static String name="name";

    public interface ErrorType{
        public static final int ERROR_NO_INTERNET_CONNECTION = 0;
        public static final int ERROR_INVALID_EMAIL = 2;
        public static final int ERROR_INVALID_PASSWORD = 3;
        public static final int ERROR_INVALID_NAME = 1;
        public static final int ERROR_INVALID_ADDRESS = 5;
        public static final int ERROR_INVALID_MOBILE = 4;
        public static final int ERROR_EMPTY_EMAIL = 6;
        public static final int ERROR_EMPTY_PASSWORD=7;

    }


}
