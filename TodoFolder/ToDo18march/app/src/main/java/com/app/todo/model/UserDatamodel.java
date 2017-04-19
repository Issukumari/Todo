package com.app.todo.model;

public class UserDatamodel {
    private String EmailRegistration;
    private String PasswordRegistration;
    private String NameRegistration;
    private String MobileRegistration;
    private String addressResgistration;



    public UserDatamodel() {
    }

    public String getEmailRegistration() {
        return EmailRegistration;
    }

    public String getPasswordRegistration() {
        return PasswordRegistration;
    }

    public String getNameRegistration() {
        return NameRegistration;
    }

    public String getMobileRegistration() {
        return MobileRegistration;
    }

    public String getaddressResgistration() {
        return addressResgistration;
    }

    public void setEmailRegistration(String EmailRegistration) {
        this.EmailRegistration = EmailRegistration;
    }

    public void setPasswordRegistration(String PasswordRegistration) {
        this.PasswordRegistration = PasswordRegistration;
    }

    public void setNameRegistration(String NameRegistration) {
        this.NameRegistration = NameRegistration;
    }

    public void setMobileRegistration(String MobileRegistration) {
        this.MobileRegistration = MobileRegistration;
    }

    public void setaddressResgistration(String addressResgistration) {
        this.addressResgistration = addressResgistration;
    }
}


