package com.app.todo.registration.presenter;

import android.content.Context;

import com.app.todo.registration.interactor.RegistrationInteractor;
import com.app.todo.registration.interactor.RegistrationInteractorInterface;
import com.app.todo.registration.model.UserDatamodel;
import com.app.todo.registration.ui.RegistrationActivityInterface;
import com.app.todo.utils.CommonUtils;
import com.app.todo.utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterationPresenter implements RegistrationPresenterInterface {
    Matcher mMatcher;
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

   /* @Override
    public void getRegistrationResponse(String editTextEmail, String editTextPasswordd, String editTextName, String editTextMobile, String editTextAddress) {
        if (editTextName.length() == 0) {
            viewinterface.showError(Constants.ErrorType.ERROR_INVALID_NAME);
            return;
        } else if (editTextEmail.length() == 0) {
            viewinterface.showError(Constants.ErrorType.ERROR_EMPTY_EMAIL);
            return;
        } else if (!CommonUtils.isValidEmail(editTextEmail)) {
            viewinterface.showError(Constants.ErrorType.ERROR_INVALID_EMAIL);
            return;
        } else if (editTextPasswordd.length() == 0 || editTextPasswordd.length() > 10) {
            viewinterface.showError(Constants.ErrorType.ERROR_EMPTY_PASSWORD);
            return;
        } else if (mMatcher.matches() == false) {
            Pattern mPattern = Pattern.compile(Constants.Password_Pattern);
            viewinterface.showError(Constants.ErrorType.ERROR_INVALID_PASSWORD);
        } else if (editTextMobile.length() == 0) {
            viewinterface.showError(Constants.ErrorType.ERROR_INVALID_MOBILE);
            return;
        } else if (editTextMobile.length() <= 10) {
            viewinterface.showError(Constants.ErrorType.ERROR_INVALID_MOBILE);
            return;
        } else if (editTextAddress.length() == 0) {
            viewinterface.showError(Constants.ErrorType.ERROR_INVALID_ADDRESS);
            return;
        } else if (!CommonUtils.isNetworkConnected(context)) {
            viewinterface.showError(Constants.ErrorType.ERROR_NO_INTERNET_CONNECTION);
            return;
        } else {
            interactor.getRegistrationResponse(editTextEmail, editTextPasswordd, editTextName, editTextMobile, editTextAddress);
        }
    }*/
}

