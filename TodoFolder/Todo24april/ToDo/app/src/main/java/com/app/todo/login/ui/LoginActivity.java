package com.app.todo.login.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.base.BaseActivity;
import com.app.todo.registration.model.UserDatamodel;
import com.app.todo.login.presenter.LoginPresenter;
import com.app.todo.registration.ui.RegisterationActivity;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.utils.CommonUtils;
import com.app.todo.utils.Constants;


public class LoginActivity extends BaseActivity implements LoginActivityInterface {
    private static final String TAG = "LoginActivity";
    AppCompatEditText editTextEmailLogin;
    AppCompatEditText editTextPasswordLogin;
    AppCompatButton buttonLogin;
    ProgressDialog progressDialog;
    AppCompatButton buttonCreateAccount;
    AppCompatButton facebookbutton;
    AppCompatButton googlebutton;
    LoginPresenter presenter;
    private SharedPreferences sharedpreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        initView();

        if (CommonUtils.isNetworkConnected(LoginActivity.this)) {

        } else {
            new AlertDialog.Builder(this)
                    .setTitle(Constants.setTitle)
                    .setMessage(Constants.setMessage)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }

    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void initView() {
        presenter = new LoginPresenter(this, this);
        editTextEmailLogin = (AppCompatEditText) findViewById(R.id.editTextEmaillogin);
        editTextPasswordLogin = (AppCompatEditText) findViewById(R.id.editTextPasswordlogin);
        buttonLogin = (AppCompatButton) findViewById(R.id.buttonLogin);
        buttonCreateAccount = (AppCompatButton) findViewById(R.id.buttonCreateAccount);
        facebookbutton = (AppCompatButton) findViewById(R.id.facebook);
        googlebutton = (AppCompatButton) findViewById(R.id.google);
        setListeners();

    }

    @Override
    public void setListeners() {
        buttonLogin.setOnClickListener(this);
        buttonCreateAccount.setOnClickListener(this);
        facebookbutton.setOnClickListener(this);
        googlebutton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:

                presenter.getLoginResponse(editTextEmailLogin.getText().toString().trim(), editTextPasswordLogin.getText().toString().trim());
                break;
            case R.id.buttonCreateAccount:
                Intent intent = new Intent(LoginActivity.this, RegisterationActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void loginSuccess(UserDatamodel Model, String uid) {
        sharedpreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(getString(R.string.email), editTextEmailLogin.getText().toString().trim());
        editor.putString(getString(R.string.password), editTextPasswordLogin.getText().toString().trim());
        editor.putString("uid",uid);
        editor.commit();
        startActivity(new Intent(this, TodoHomeActivity.class));
        finish();

    }

    @Override
    public void loginFailure(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressDialog(String message) {
        if (!isFinishing() && progressDialog != null) {
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (!isFinishing()) {
            if (progressDialog != null)
                progressDialog.dismiss();
        }
    }

    @Override
    public void showError(int errorType) {
        switch (errorType) {
            case Constants.ErrorType.ERROR_EMPTY_EMAIL:
                editTextEmailLogin.setError(getString(R.string.email_should_not_be_empty));
                editTextEmailLogin.requestFocus();
                break;
            case Constants.ErrorType.ERROR_INVALID_EMAIL:
                editTextEmailLogin.setError(getString(R.string.invalid_Email));
                editTextEmailLogin.requestFocus();
                break;

            case Constants.ErrorType.ERROR_EMPTY_PASSWORD:
                editTextPasswordLogin.setError(getString(R.string.Password_should_not_empty));
                editTextPasswordLogin.requestFocus();
                break;
            case Constants.ErrorType.ERROR_INVALID_PASSWORD:
                editTextPasswordLogin.setError(getString(R.string.invalid_Password));
                editTextPasswordLogin.requestFocus();
            case Constants.ErrorType.ERROR_NO_INTERNET_CONNECTION:
                Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
