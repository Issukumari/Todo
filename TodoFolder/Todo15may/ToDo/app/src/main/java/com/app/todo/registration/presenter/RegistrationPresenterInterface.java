package com.app.todo.registration.presenter;

import com.app.todo.registration.model.UserDatamodel;

/**
 * Created by bridgeit on 20/4/17.
 */
public interface RegistrationPresenterInterface {
    void  registrationSuccess(String message);
    void registrationFailure(String message);
    void showProgressDialog(String mesage);
    void hideProgressDialog();
    void getRegistrationResponse(UserDatamodel datamodel);


}