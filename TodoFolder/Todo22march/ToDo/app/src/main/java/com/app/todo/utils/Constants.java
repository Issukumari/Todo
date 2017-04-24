package com.app.todo.utils;

/**
 * Created by bridgeit on 26/3/17.
 */
public class Constants {
    public static int Timeout = 5000;
    public static String keys = "smita";
    public static String values = "";
    public static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static String Password_Pattern ="^([a-zA-Z0-9@*#]{8,15})$";
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

    public interface ErrorType{
        public static final int ERROR_NO_INTERNET_CONNECTION = 0;
        public static final int ERROR_INVALID_EMAIL = 1;
        public static final int ERROR_INVALID_PASSWORD = 2;
        public static final int ERROR_INVALID_NAME = 3;
        public static final int ERROR_INVALID_ADDRESS = 4;
        public static final int ERROR_INVALID_MOBILE = 5;
        public static final int ERROR_EMPTY_EMAIL = 6;
        public static final int ERROR_EMPTY_PASSWORD=7;

    }


}