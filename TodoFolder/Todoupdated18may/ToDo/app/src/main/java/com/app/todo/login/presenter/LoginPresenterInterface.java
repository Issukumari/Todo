package com.app.todo.login.presenter;

import android.os.Bundle;

import com.app.todo.registration.model.UserDatamodel;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bridgeit on 21/4/17.
 */
public interface LoginPresenterInterface {
    void loginSuccess(UserDatamodel model, String uid);

    void loginFailure(String message);

    void showProgressDialog(String mesage);

    void hideProgressDialog();

    void getLoginResponse(String email_login, String password_login);

    void getfacebookResponse(CallbackManager callbackManager, LoginButton loginButton);

    void fbFailure(String message);


    void getfacebookdata(String uid, JSONObject jsonObject) throws JSONException;
}
