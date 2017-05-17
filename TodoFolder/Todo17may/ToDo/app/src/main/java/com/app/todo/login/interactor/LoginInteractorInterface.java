package com.app.todo.login.interactor;


import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

public interface LoginInteractorInterface {

    void getLoginResponse(String email_login, String password_login);

    void getfacebookResponse(CallbackManager callbackManager, LoginButton loginButton);


}
