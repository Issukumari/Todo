package com.app.todo.login.ui;

import android.view.View;

import com.app.todo.registration.model.UserDatamodel;

/**
 * Created by bridgeit on 21/4/17.
 */
public interface LoginActivityInterface extends View.OnClickListener {
    void loginSuccess(UserDatamodel Model, String uid);
    void loginFailure(String message);
    void showProgressDialog(String mesage);
    void hideProgressDialog();

    void showError(int errorType);

}
