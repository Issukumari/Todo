package com.app.todo.registration.presenter;

import android.content.Context;

import com.app.todo.registration.interactor.RegistrationInteractor;
import com.app.todo.registration.interactor.RegistrationInteractorInterface;
import com.app.todo.registration.model.UserDatamodel;
import com.app.todo.registration.ui.RegistrationActivityInterface;

public class RegisterationPresenter implements RegistrationPresenterInterface {

    Context context;
    RegistrationActivityInterface viewinterface;

    RegistrationInteractorInterface interactor;

    public RegisterationPresenter(Context context, RegistrationActivityInterface viewinterface) {
        this.context = context;
        this.viewinterface = viewinterface;

        interactor = new RegistrationInteractor(context, this);
    }


    @Override
    public void registrationSuccess(String message) {
        viewinterface.registrationSuccess(message);
    }

    @Override
    public void registrationFailure(String message) {
        viewinterface.registrationFailure(message);
    }

    @Override
    public void showProgressDialog(String mesage) {
        viewinterface.showProgressDialog(mesage);
    }

    @Override
    public void hideProgressDialog() {
        viewinterface.hideProgressDialog();
    }

    @Override
    public void getRegistrationResponse(UserDatamodel datamodel) {
        interactor.getRegistrationResponse(datamodel);
    }
}
