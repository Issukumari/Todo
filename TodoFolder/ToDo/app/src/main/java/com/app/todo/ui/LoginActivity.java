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

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "LoginActivity";
    AppCompatEditText editTextEmailLogin;
    AppCompatEditText editTextPasswordLogin;
    AppCompatButton buttonLogin;
    AppCompatButton buttonCreateAccount;
    Pattern pattern,pattern2;
    Matcher matcher,matcher2;
    String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String Password_Pattern= "^([a-zA-Z0-9@*#]{8,15})$";


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

                /*Intent intent1 = new Intent(LoginActivity.this, TodoHomeActivity.class);
                startActivity(intent1);

*/
                      SharedPreferences sharedPreferences = getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
                      String email_login = sharedPreferences.getString("email", Constants.values);
                     String  password_login = sharedPreferences.getString("password", Constants.values);
                pattern= Pattern.compile(EMAIL_PATTERN);
                matcher=pattern.matcher(editTextEmailLogin.getText().toString());

                pattern2= Pattern.compile(Password_Pattern);
                matcher2=pattern2.matcher(editTextPasswordLogin.getText().toString());

                if(editTextEmailLogin.getText().toString().length()==0){
                    editTextEmailLogin.setError("Email should not empty");
                    editTextEmailLogin.requestFocus();
                    //editTextPassword.setError("Valid pswrd");
                }
                else if(matcher.matches()){

                }
                else{
                    editTextEmailLogin.setError("invalid Emailid");
                    editTextEmailLogin.requestFocus();
                }

                if(editTextPasswordLogin.getText().toString().length()==0){
                    editTextPasswordLogin.setError("Password should not empty");
                    editTextPasswordLogin.requestFocus();
                }
                else if(matcher2.matches()){


                }
                else{
                    editTextPasswordLogin.setError("invalid Password");
                    editTextPasswordLogin.requestFocus();
                }
                if (editTextEmailLogin.getText().toString().equalsIgnoreCase(email_login) && editTextPasswordLogin.getText().toString().equalsIgnoreCase(password_login)) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_successfull), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, TodoHomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "enter your details or create Account", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.buttonCreateAccount :
                Intent intent = new Intent(LoginActivity.this, RegisterationActivity.class);
                startActivity(intent);
                break;
        }
    }
}
