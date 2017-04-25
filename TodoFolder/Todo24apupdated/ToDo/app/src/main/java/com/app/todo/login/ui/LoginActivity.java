package com.app.todo.login.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Base64;
import android.util.Log;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Target;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


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
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        AppEventsLogger.activateApp(this);
        initView();
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.app.todo", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }





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
        loginButton.setReadPermissions("public_profile email");
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        getFbLogin();
        setListeners();

    }

    public void getFbLogin() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override

                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Bundle bFacebookData = getFacebookData(object);

                        String emailid = bFacebookData.getString("email");
                        editor.putString("email", emailid);
                        editor.putString("profile", bFacebookData.getString("profile_pic"));
                        editor.putString("firstname", bFacebookData.getString("first_name"));
                        editor.putString("lastname", bFacebookData.getString("last_name"));
                        editor.commit();
                        Toast.makeText(LoginActivity.this, "Welcome :" + bFacebookData.getString("first_name"), Toast.LENGTH_SHORT).show();

                    }

                    private Bundle getFacebookData(JSONObject object) {
                        try {

                            Bundle bundle = new Bundle();

                            String id = object.getString("id");

                            try {
                                URL profile_pic = new URL("http://graph.facebook.com/" + id + "/picture?type=square");

                                Log.i("profile_pic", profile_pic + "");

                                bundle.putString("profile_pic", profile_pic.toString());

                            } catch (MalformedURLException e) {

                                e.printStackTrace();

                                return null;

                            }

                            bundle.putString("idFacebook", id);

                            if (object.has("first_name"))

                                bundle.putString("first_name", object.getString("first_name"));

                            if (object.has("last_name"))

                                bundle.putString("last_name", object.getString("last_name"));

                            if (object.has("email"))

                                bundle.putString("email", object.getString("email"));

                            return bundle;

                        } catch (JSONException e) {

                            return null;

                        }

                    }


                });

                Bundle parameters = new Bundle();

                parameters.putString("fields", "id, first_name, last_name, email"); 

                request.setParameters(parameters);

                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this,("hjfd"), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();

            }
            
        });}



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();
                            editor.putString("uid",uid);
                            startActivity(new Intent(getApplicationContext(),TodoHomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "auth_failed",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
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
