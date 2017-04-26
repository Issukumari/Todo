package com.app.todo.login.presenter;

import android.content.Context;

import com.app.todo.login.ui.LoginActivity;
import com.app.todo.login.interactor.LoginInteractor;
import com.app.todo.login.interactor.LoginInteractorInterface;
import com.app.todo.login.ui.LoginActivityInterface;
import com.app.todo.registration.model.UserDatamodel;
import com.app.todo.utils.CommonUtils;
import com.app.todo.utils.Constants;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginPresenter implements LoginPresenterInterface {
    Context context;
    LoginActivityInterface viewinterface;

    LoginInteractorInterface interactor;

    public LoginPresenter(Context context, LoginActivity viewinterface) {
        this.context = context;
        this.viewinterface = viewinterface;

        interactor = new LoginInteractor(context, this);
    }


    @Override
    public void loginSuccess(UserDatamodel model, String uid) {
        viewinterface.loginSuccess(model,uid);
    }

    @Override
    public void loginFailure(String message) {

        viewinterface.loginFailure(message);
    }

    @Override
    public void showProgressDialog(String message) {
        viewinterface.showProgressDialog(message);
    }

    @Override
    public void hideProgressDialog() {
        viewinterface.hideProgressDialog();

    }

    @Override
    public void getLoginResponse(String email_login, String password_login) {


             if (email_login.length() == 0) {
                viewinterface.showError(Constants.ErrorType.ERROR_EMPTY_EMAIL);
            return;
            }
      else  if (!CommonUtils.isValidEmail(email_login)) {
            viewinterface.showError(Constants.ErrorType.ERROR_INVALID_EMAIL);
            return;
        }
        else if (password_login.length() == 0 || password_login.length() > 10) {
            viewinterface.showError(Constants.ErrorType.ERROR_EMPTY_PASSWORD);
        } else if (!CommonUtils.isNetworkConnected(context)) {
            viewinterface.showError(Constants.ErrorType.ERROR_NO_INTERNET_CONNECTION);
            return;
        } else {
            interactor.getLoginResponse(email_login, password_login);
        }
    }

    @Override
    public void getfacebookResponse(CallbackManager callbackManager, LoginButton loginButton) {
        interactor.getfacebookResponse(callbackManager,loginButton);
    }


    @Override
    public void fbFailure(String message) {
viewinterface.fbFailure(message);
    }

    @Override
    public void getfacebookdata(String uid, JSONObject jsonObject) throws JSONException {
        viewinterface. getfacebookdata(uid, jsonObject);
    }


}

