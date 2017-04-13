package com.app.todo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.base.BaseActivity;
import com.app.todo.utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    AppCompatEditText editTextEmailLogin;
    AppCompatEditText editTextPasswordLogin;
    AppCompatButton buttonLogin;
    AppCompatButton buttonCreateAccount;
    Pattern EmailPattern, Passwordpattern;
    Matcher mMatcher, matcher;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    @Override
    public void initView() {
        editTextEmailLogin = (AppCompatEditText) findViewById(R.id.editTextEmaillogin);
        editTextPasswordLogin = (AppCompatEditText) findViewById(R.id.editTextPasswordlogin);
        buttonLogin = (AppCompatButton) findViewById(R.id.buttonLogin);
        buttonCreateAccount = (AppCompatButton) findViewById(R.id.buttonCreateAccount);
        setListeners();
    }

    @Override
    public void setListeners() {
        buttonLogin.setOnClickListener(this);
        buttonCreateAccount.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                validate();
                break;

            case R.id.buttonCreateAccount:
                Intent intent = new Intent(LoginActivity.this, RegisterationActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void validate() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        String email_login = sharedPreferences.getString("email", Constants.values);
        String password_login = sharedPreferences.getString("password", Constants.values);
        EmailPattern = Pattern.compile(Constants.EMAIL_PATTERN);
        mMatcher = EmailPattern.matcher(editTextEmailLogin.getText().toString());

        Passwordpattern = Pattern.compile(Constants.Password_Pattern);
        matcher = Passwordpattern.matcher(editTextPasswordLogin.getText().toString());

        if (editTextEmailLogin.getText().toString().length() == 0) {
            editTextEmailLogin.setError(getString(R.string.Emailid_should_not_empty));
            editTextEmailLogin.requestFocus();
        } else if (mMatcher.matches()) {

        } else {
            editTextEmailLogin.setError(getString(R.string.invalid_Emailid));
            editTextEmailLogin.requestFocus();
        }

        if (editTextPasswordLogin.getText().toString().length() == 0) {
            editTextPasswordLogin.setError(getString(R.string.Password_should_not_empty));
            editTextPasswordLogin.requestFocus();
        } else if (matcher.matches()) {


        } else {
            editTextPasswordLogin.setError("invalid Password");
            editTextPasswordLogin.requestFocus();
        }
        if (editTextEmailLogin.getText().toString().equalsIgnoreCase(email_login) && editTextPasswordLogin.getText().toString().equalsIgnoreCase(password_login)) {
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_successfull), Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("islogin", true);
            editor.commit();
            Intent intent = new Intent(LoginActivity.this, TodoHomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, R.string.details, Toast.LENGTH_LONG).show();
        }

    }
}

