package com.app.todo.registration.model;

public class UserDatamodel {
    public String emailRegistration;
    public String passwordRegistration;
    public String nameRegistration;
    public String mobileRegistration;
    public String addressResgistration;



    public UserDatamodel() {
    }

    public String getEmailRegistration() {
        return emailRegistration;
    }

    public String getPasswordRegistration() {
        return passwordRegistration;
    }

    public String getNameRegistration() {
        return nameRegistration;
    }

    public String getMobileRegistration() {
        return mobileRegistration;
    }

    public String getaddressResgistration() {
        return addressResgistration;
    }

    public void setEmailRegistration(String EmailRegistration) {
        this.emailRegistration = EmailRegistration;
    }

    public void setPasswordRegistration(String PasswordRegistration) {
        this.passwordRegistration = PasswordRegistration;
    }

    public void setNameRegistration(String NameRegistration) {
        this.nameRegistration = NameRegistration;
    }

    public void setMobileRegistration(String MobileRegistration) {
        this.mobileRegistration = MobileRegistration;
    }

    public void setaddressResgistration(String addressResgistration) {
        this.addressResgistration = addressResgistration;
    }
}


