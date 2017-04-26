package com.app.todo.login.ui;

import android.os.Bundle;
import android.view.View;

import com.app.todo.registration.model.UserDatamodel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bridgeit on 21/4/17.
 */
public interface LoginActivityInterface extends View.OnClickListener {
    void loginSuccess(UserDatamodel Model, String uid);
    void loginFailure(String message);
    void showProgressDialog(String mesage);
    void hideProgressDialog();
    void fbFailure(String message);
    void showError(int errorType);
    void getfacebookdata(String uid, JSONObject jsonObject) throws JSONException;
}
