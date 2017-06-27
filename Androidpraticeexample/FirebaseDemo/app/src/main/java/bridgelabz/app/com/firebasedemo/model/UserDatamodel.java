package bridgelabz.app.com.firebasedemo.model;

import android.support.v7.widget.AppCompatEditText;

/**
 * Created by bridgeit on 15/4/17.
 */
public class UserDatamodel {
    private String id;
    private AppCompatEditText editTextEmailLogin;
    private AppCompatEditText editTextPasswordLogin;
    private AppCompatEditText editTextNameRegisteration;

    private AppCompatEditText editTextEmailRegisteration;
    private AppCompatEditText editTextPwdRegisteration;
    private AppCompatEditText editTextMobileRegisteration;
    private AppCompatEditText editTextAddressRegisteration;

    public UserDatamodel(AppCompatEditText editTextEmailLogin, AppCompatEditText editTextPasswordLogin) {
        this.editTextEmailLogin = editTextEmailLogin;
        this.editTextPasswordLogin = editTextPasswordLogin;

    }

    public UserDatamodel(AppCompatEditText id, AppCompatEditText editTextNameRegisteration, AppCompatEditText editTextEmailRegisteration, AppCompatEditText editTextPwdRegisteration, AppCompatEditText editTextMobileRegisteration, AppCompatEditText editTextAddressRegisteration) {
        this.editTextNameRegisteration = editTextNameRegisteration;
        this.editTextEmailRegisteration = editTextEmailRegisteration;
        this.editTextPwdRegisteration = editTextPwdRegisteration;
        this.editTextMobileRegisteration = editTextMobileRegisteration;
        this.editTextAddressRegisteration = editTextAddressRegisteration;
    }

    public AppCompatEditText getEditTextEmailLogin() {
        return editTextEmailLogin;
    }

    public void setEditTextEmailLogin(AppCompatEditText editTextEmailLogin) {
        this.editTextEmailLogin = editTextEmailLogin;
    }

    public AppCompatEditText getEditTextPasswordLogin() {
        return editTextPasswordLogin;

    }

    public void setEditTextPasswordLogin(AppCompatEditText editTextPasswordLogin) {
        this.editTextPasswordLogin = editTextPasswordLogin;
    }

    public AppCompatEditText getEditTextNameRegisteration() {

        return editTextNameRegisteration;
    }

    public void setEditTextNameRegisteration(AppCompatEditText editTextNameRegisteration) {
        this.editTextNameRegisteration = editTextNameRegisteration;
    }

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public AppCompatEditText getEditTextEmailRegisteration() {

        return editTextEmailRegisteration;
    }

    public void setEditTextEmailRegisteration(AppCompatEditText editTextEmailRegisteration) {
        this.editTextEmailRegisteration = editTextEmailRegisteration;
    }

    public AppCompatEditText getEditTextPwdRegisteration() {
        return editTextPwdRegisteration;
    }

    public void setEditTextPwdRegisteration(AppCompatEditText editTextPwdRegisteration) {
        this.editTextPwdRegisteration = editTextPwdRegisteration;
    }

    public AppCompatEditText getEditTextMobileRegisteration() {
        return editTextMobileRegisteration;
    }

    public void setEditTextMobileRegisteration(AppCompatEditText editTextMobileRegisteration) {
        this.editTextMobileRegisteration = editTextMobileRegisteration;
    }

    public AppCompatEditText getEditTextAddressRegisteration() {
        return editTextAddressRegisteration;
    }

    public void setEditTextAddressRegisteration(AppCompatEditText editTextAddressRegisteration) {
        this.editTextAddressRegisteration = editTextAddressRegisteration;
    }
}


