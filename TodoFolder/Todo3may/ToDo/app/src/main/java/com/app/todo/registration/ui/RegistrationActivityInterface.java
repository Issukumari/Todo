package com.app.todo.registration.ui;

import android.view.View;

/**
 * Created by bridgeit on 20/4/17.
 */
public interface RegistrationActivityInterface extends View.OnClickListener {
    void  registrationSuccess(String message);
    void registrationFailure(String message);
    void showProgressDialog(String mesage);
    void hideProgressDialog();
    //void showError(int errorType);
}
