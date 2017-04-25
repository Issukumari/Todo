package com.app.todo.registration.interactor;

import com.app.todo.registration.model.UserDatamodel;

/**
 * Created by bridgeit on 20/4/17.
 */
public interface RegistrationInteractorInterface {
    void getRegistrationResponse(UserDatamodel datamodel);

    //void getRegistrationResponse(String editTextEmail, String editTextPasswordd, String editTextName, String editTextMobile, String editTextAddress);
}