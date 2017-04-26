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
import com.app.todo.login.presenter.LoginPresenter;
import com.app.todo.registration.model.UserDatamodel;
import com.app.todo.registration.ui.RegisterationActivity;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.utils.CommonUtils;
import com.app.todo.utils.Constants;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends BaseActivity implements LoginActivityInterface {
    private static final String TAG = "LoginActivity";
    AppCompatEditText editTextEmailLogin;
    AppCompatEditText editTextPasswordLogin;
    AppCompatButton buttonLogin;
    ProgressDialog progressDialog;
    AppCompatButton buttonCreateAccount;
    AppCompatButton googlebutton;
    LoginPresenter presenter;
    private SharedPreferences sharedpreferences;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private SharedPreferences.Editor editor;
    String firstname,lastname,email,imageUrl;
    private SharedPreferences sharedPreferences;
    View hView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        AppEventsLogger.activateApp(this);
        initView();
        progressDialog = new ProgressDialog(this);


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
        loginButton = (LoginButton) findViewById(R.id.login_button);
        googlebutton = (AppCompatButton) findViewById(R.id.google);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("public_profile", "email");
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setListeners();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setListeners() {
        buttonLogin.setOnClickListener(this);
        buttonCreateAccount.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        googlebutton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                presenter.getLoginResponse(editTextEmailLogin.getText().toString().trim(), editTextPasswordLogin.getText().toString().trim());
                break;
            case R.id.login_button:
                presenter.getfacebookResponse(callbackManager, loginButton);
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
        editor.putString("uid", uid);
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
    public void fbFailure(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

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

    @Override
    public void getfacebookdata(String uid, JSONObject jsonObject) throws JSONException {
        sharedpreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        String emailid = jsonObject.getString("email");
        editor.putString("email", emailid);
        /*editor.putString("profile", jsonObject.getString("profile_pic"));*/
        editor.putString("firstname", jsonObject.getString("first_name"));
        editor.putString("lastname", jsonObject.getString("last_name"));
        editor.putString("id", jsonObject.getString("id"));
        editor.putString("uid", uid);

        /*Toast.makeText(LoginActivity.this, jsonObject.getString("profile_pic"), Toast.LENGTH_SHORT).show();*/
        editor.commit();
        /*firstname = sharedPreferences.getString("firstname", "value");
        lastname = sharedPreferences.getString("lastname", "value");
        email = sharedPreferences.getString("email", "value");
        imageUrl = sharedPreferences.getString("profile", "value");
        Navigationheadername.setText(firstname + " " + lastname);
        Navigationheaderid.setText(email);
        Picasso.with(getApplicationContext()).load(imageUrl).into(circleimageview);*/
        startActivity(new Intent(this, TodoHomeActivity.class));
        finish();
        Toast.makeText(LoginActivity.this, "Welcome :" + jsonObject.getString("first_name"), Toast.LENGTH_SHORT).show();

    }
}

