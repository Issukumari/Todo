package com.app.todo.registration.ui;

import android.view.View;

import com.app.todo.registration.model.UserDatamodel;

/**
 * Created by bridgeit on 20/4/17.
 */
public interface RegistrationActivityInterface extends View.OnClickListener {
    void  registrationSuccess(UserDatamodel datamodel, String message);
    void registrationFailure(String message);
    void showProgressDialog(String mesage);
    void hideProgressDialog();
}
