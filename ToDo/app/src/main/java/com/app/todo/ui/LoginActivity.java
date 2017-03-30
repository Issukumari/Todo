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

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "LoginActivity";
    AppCompatEditText editTextEmailLogin;
    AppCompatEditText editTextPasswordLogin;
    AppCompatButton buttonLogin;
    AppCompatButton buttonCreateAccount;


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
                      SharedPreferences sharedPreferences = getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
                      String email_login = sharedPreferences.getString("email", Constants.values);
                     String  password_login = sharedPreferences.getString("password", Constants.values);

                if (editTextEmailLogin.getText().toString().equalsIgnoreCase(email_login) && editTextPasswordLogin.getText().toString().equalsIgnoreCase(password_login)) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_successfull), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "invalid id or password", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.buttonCreateAccount :
                Intent intent = new Intent(LoginActivity.this, RegisterationActivity.class);
                startActivity(intent);
                break;
        }
    }
}
