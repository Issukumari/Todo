package com.app.todo.login.presenter;

import com.app.todo.registration.model.UserDatamodel;

/**
 * Created by bridgeit on 21/4/17.
 */
public interface LoginPresenterInterface {
    void loginSuccess(UserDatamodel model, String uid);
    void loginFailure(String message);
    void showProgressDialog(String mesage);
    void hideProgressDialog();
    void getLoginResponse( String email_login,String password_login);

}
