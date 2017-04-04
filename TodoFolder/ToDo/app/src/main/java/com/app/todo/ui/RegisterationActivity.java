
package com.app.todo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.base.BaseActivity;
import com.app.todo.utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterationActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RegistrationActivity";
    AppCompatEditText editTextNameRegisteration;
    AppCompatEditText editTextEmailRegisteration;
    AppCompatEditText editTextPwdRegisteration;
    AppCompatEditText editTextMobileRegisteration;
    AppCompatEditText editTextAddressRegisteration;
    AppCompatButton    saveButton;
    AppCompatButton AlreadyAccountButton;

       Matcher mMatcher;
       Pattern Pattern;
       String PASSWORD_PATTERN = "^([a-zA-Z0-9@*#]{8,15})$";

          @Override
          public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_registration);
              initView();
       }
              @Override
              public void initView() {
              editTextNameRegisteration = (AppCompatEditText) findViewById(R.id.editTextName_Registeration);
              editTextEmailRegisteration = (AppCompatEditText) findViewById(R.id.editTextEmail_Registeration);
              editTextPwdRegisteration = (AppCompatEditText) findViewById(R.id.editTextPwd_Registeration);
              editTextMobileRegisteration = (AppCompatEditText) findViewById(R.id.editTextMobile_Registeration);
              editTextAddressRegisteration = (AppCompatEditText) findViewById(R.id.editTextAddress_Registeration);
              saveButton = (AppCompatButton) findViewById(R.id.SaveButton);
                  AlreadyAccountButton= (AppCompatButton) findViewById(R.id.AlreadyAccountButton);
                  setListeners();
    }

    @Override
       public void setListeners() {

        saveButton.setOnClickListener(this);
        AlreadyAccountButton.setOnClickListener(this);

    }




          @Override
         public void onClick(View view) {
              switch (view.getId()) {
                  case R.id.SaveButton:
                      validate();
                      break;
                  case  R.id.AlreadyAccountButton:
                      Intent intent=new Intent(RegisterationActivity.this,LoginActivity.class);
                      startActivity(intent);
                      break;

              }
          }

         public  void validate() {
            boolean checkemail = false, checkname = false, checkpassword = false, checkMobile = false, checkAddress = false;

             Pattern mPattern=Pattern.compile(PASSWORD_PATTERN);
             mMatcher = mPattern.matcher(editTextPwdRegisteration.getText().toString());

            SharedPreferences sh = getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
            String name = editTextNameRegisteration.getText().toString();
            String email = editTextEmailRegisteration.getText().toString();
            String password = editTextPwdRegisteration.getText().toString();
            String Mobile = editTextMobileRegisteration.getText().toString();
            String Adress = editTextAddressRegisteration.getText().toString();

            if (name.isEmpty()) {
                editTextNameRegisteration.setError("name should not empty");

            } else {
                checkname = true;
            }


            if (email.isEmpty()) {
                editTextEmailRegisteration.setError("email should not empty");

            } else if (!isValidEmail(email)) {
                editTextEmailRegisteration.setError("invalid Email");
            } else {
                checkemail = true;
            }


            if (password.isEmpty()) {
                editTextPwdRegisteration.setError("password should not empty");

            } else if (password.length() < 5) {
                editTextPwdRegisteration.setError("password should be minimum 5 character");

            }
            else if(mMatcher.matches()) {
                checkpassword = true;
            }
            else  {
                editTextPwdRegisteration.setError("wrong format of password");
                        editTextPwdRegisteration.requestFocus();

            }

            if (Mobile.isEmpty()) {
                editTextMobileRegisteration.setError("mobile no  should not be empty");
            } else if (Mobile.length() < 10) {
                editTextMobileRegisteration.setError("mobile no should be 10 digit");
            } else {
                checkMobile = true;
            }

            if (Adress.isEmpty()) {
                editTextAddressRegisteration.setError("name should not empty");

            } else {
                checkAddress = true;
            }
            if (checkname && checkMobile && checkAddress && checkemail && checkpassword) {
                SharedPreferences.Editor editor = sh.edit();
                editor.putString("Name", name);
                editor.putString("email", email);
                editor.putString("password", password);
                editor.putString("Mobile", Mobile);
                editor.putString("Adress", Adress);
                editor.commit();
                Toast.makeText(getApplicationContext(), "RegisterActivity Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();

            }

        }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    }

