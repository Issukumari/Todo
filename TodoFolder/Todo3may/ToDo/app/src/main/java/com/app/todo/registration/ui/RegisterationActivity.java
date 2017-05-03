package com.app.todo.registration.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.base.BaseActivity;
import com.app.todo.login.ui.LoginActivity;
import com.app.todo.registration.model.UserDatamodel;
import com.app.todo.registration.presenter.RegisterationPresenter;
import com.app.todo.utils.CommonUtils;
import com.app.todo.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterationActivity extends BaseActivity implements RegistrationActivityInterface {

    private static final String TAG = "RegistrationActivity";
    RegisterationPresenter presenter;
    AppCompatEditText editTextName;
    AppCompatEditText editTextEmail;
    AppCompatEditText editTextPasswordd;
    AppCompatEditText editTextMobile;
    AppCompatEditText editTextAddress;
    AppCompatButton Buttonsave;
    AppCompatButton AlreadyAccountButton;
    Matcher mMatcher;
    java.util.regex.Pattern Pattern;
    ProgressDialog progressDialog;
    DatabaseReference mfirebasedatabase;
    UserDatamodel dataModel;
    String nameTxt, emailTxt, passwordTxt, mobileTxt, addressTxt;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        presenter = new RegisterationPresenter(this, this);
        initView();
    }

    @Override
    public void initView() {
        editTextName = (AppCompatEditText) findViewById(R.id.editTextName_Registeration);
        editTextEmail = (AppCompatEditText) findViewById(R.id.editTextEmail_Registeration);
        editTextPasswordd = (AppCompatEditText) findViewById(R.id.editTextPwd_Registeration);
        editTextMobile = (AppCompatEditText) findViewById(R.id.editTextMobile_Registeration);
        editTextAddress = (AppCompatEditText) findViewById(R.id.editTextAddress_Registeration);
        Buttonsave = (AppCompatButton) findViewById(R.id.SaveButton);
        AlreadyAccountButton = (AppCompatButton) findViewById(R.id.AlreadyAccountButton);
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference("");
        progressDialog = new ProgressDialog(this);
        setListeners();
    }

    @Override
    public void setListeners() {
        Buttonsave.setOnClickListener(this);
        AlreadyAccountButton.setOnClickListener(this);
    }

    public boolean validate() {
        boolean checkemail = false, checkname = false, checkpassword = false, checkMobile = false, checkAddress = false;
        Pattern mPattern = Pattern.compile(Constants.Password_Pattern);
        mMatcher = mPattern.matcher(editTextPasswordd.getText().toString());
        String nameTxt = editTextName.getText().toString();
        passwordTxt = editTextPasswordd.getText().toString();
        emailTxt = editTextEmail.getText().toString();
        mobileTxt = editTextMobile.getText().toString();
        addressTxt = editTextAddress.getText().toString();
        if (nameTxt.isEmpty()) {
            editTextName.setError(getString(R.string.name_should_not_be_empty));
        } else {
            checkname = true;
        }

        if (emailTxt.isEmpty()) {
            editTextEmail.setError(getString(R.string.Emailid_should_not_be_empty));

        } else if (!CommonUtils.isValidEmail(emailTxt)) {
            editTextEmail.setError(getString(R.string.invalid_Email));
        } else {
            checkemail = true;
        }
        if (passwordTxt.isEmpty()) {
            editTextPasswordd.setError(getString(R.string.password_should_not_empty));
        } else if (passwordTxt.length() < 10) {
            editTextPasswordd.setError(getString(R.string.password_should_be_minimum_10_character));

        } else if (mMatcher.matches()) {
            checkpassword = true;

        } else {
            editTextPasswordd.setError(getString(R.string.wrong_format_of_password));
            editTextPasswordd.requestFocus();
        }
        if (mobileTxt.isEmpty()) {
            editTextMobile.setError(getString(R.string.mobile_no_should_not_be_empty));
        } else if (mobileTxt.length() <10) {
            editTextMobile.setError(getString(R.string.mobile_no_should_be_10_digit));
        } else {
            checkMobile = true;
        }
        if (addressTxt.isEmpty()) {
            editTextAddress.setError(getString(R.string.Address_should_not_be_empty));
        } else {
            checkAddress = true;
        }
        return checkname && checkemail && checkMobile && checkpassword && checkAddress;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SaveButton:
                if (validate()) {
               /* presenter.getRegistrationResponse(editTextEmail.getText().toString().trim(),editTextPasswordd.getText().toString().trim(),editTextName.getText().toString().trim(),editTextMobile.getText().toString().trim(),editTextEmail.getText().toString().trim(),editTextAddress.getText().toString().trim());
                    */
                    dataModel = new UserDatamodel();
                    dataModel.setEmailRegistration(emailTxt);
                    dataModel.setPasswordRegistration(passwordTxt);
                    dataModel.setNameRegistration(nameTxt);
                    dataModel.setMobileRegistration(mobileTxt);
                    dataModel.setaddressResgistration(addressTxt);
                    presenter.getRegistrationResponse(dataModel);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
                break;
            case R.id.AlreadyAccountButton:
                Intent intent = new Intent(RegisterationActivity.this, LoginActivity.class);
                startActivity(intent);
                break;

        }
    }


    @Override
    public void registrationSuccess(String message) {
        Toast.makeText(RegisterationActivity.this, message, Toast.LENGTH_SHORT).show();
        /*startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();*/
    }

    @Override
    public void registrationFailure(String message) {
        Toast.makeText(RegisterationActivity.this, message, Toast.LENGTH_SHORT).show();
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


   /* @Override
    public void showError(int errorType) {
        switch (errorType) {
            case Constants.ErrorType.ERROR_EMPTY_EMAIL:
                editTextEmail.setError(getString(R.string.email_should_not_be_empty));
                editTextEmail.requestFocus();
                break;
            case Constants.ErrorType.ERROR_INVALID_EMAIL:
                editTextEmail.setError(getString(R.string.invalid_Email));
                editTextEmail.requestFocus();
                break;
            case Constants.ErrorType.ERROR_INVALID_MOBILE:
                editTextEmail.setError(getString(R.string.mobile_no_should_not_be_empty));
                editTextEmail.requestFocus();
                break;
            case Constants.ErrorType.ERROR_INVALID_ADDRESS:
                editTextEmail.setError(getString(R.string.Address_should_not_be_empty));
                editTextEmail.requestFocus();
                break;
            case Constants.ErrorType.ERROR_INVALID_PASSWORD:
                editTextPasswordd.setError(getString(R.string.Password_should_not_empty));
                editTextPasswordd.requestFocus();
                break;
            case Constants.ErrorType.ERROR_NO_INTERNET_CONNECTION:
                Toast.makeText(RegisterationActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                break;

        }*/
}