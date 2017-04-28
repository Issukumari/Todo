package com.app.todo.login.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends BaseActivity implements LoginActivityInterface, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginActivity";
    AppCompatEditText editTextEmailLogin;
    AppCompatEditText editTextPasswordLogin;
    AppCompatButton buttonLogin;
    ProgressDialog progressDialog;
    AppCompatButton buttonCreateAccount;
    SignInButton signInButton;
    LoginPresenter presenter;
    int RC_SIGN_IN = 100;
    FirebaseDatabase firebasedatabase;
    private SharedPreferences sharedpreferences;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mfirebasedatabase;
    private LinearLayout linearLayout;

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
            Snackbar snackbar = Snackbar
                    .make(linearLayout, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
                    .setAction(R.string.RETRY, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();

        }
    }
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void initView() {
        presenter = new LoginPresenter(this, this);
        linearLayout = (LinearLayout) findViewById(R.id.layout1);
        editTextEmailLogin = (AppCompatEditText) findViewById(R.id.editTextEmaillogin);
        editTextPasswordLogin = (AppCompatEditText) findViewById(R.id.editTextPasswordlogin);
        buttonLogin = (AppCompatButton) findViewById(R.id.buttonLogin);
        buttonCreateAccount = (AppCompatButton) findViewById(R.id.buttonCreateAccount);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        callbackManager = CallbackManager.Factory.create();
        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setReadPermissions(getString(R.string.public_profile), getString(R.string.email));
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        setListeners();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String person_name = account.getDisplayName();
            String person_email = account.getEmail();
            Uri profile_pic = account.getPhotoUrl();
            String person_id = account.getId();
            sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString(getString(R.string.Name), person_name);
            editor.putString(getString(R.string.Email), person_email);
            editor.putString(getString(R.string.Id), person_id);
            editor.putString("Pic", profile_pic.toString());
            editor.putBoolean(Constants.googleloginkeys, true);
            editor.apply();
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                editor.putString("uid", task.getResult().getUser().getUid());
                                editor.commit();
                                startActivity(new Intent(getApplicationContext(), TodoHomeActivity.class));
                                finish();

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed),
                                        Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
        } else {

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {


    }


    @Override
    public void setListeners() {
        buttonLogin.setOnClickListener(this);
        buttonCreateAccount.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
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
            case R.id.google_sign_in_button:
                signIn();
                break;
            case R.id.buttonCreateAccount:
                Intent intent = new Intent(LoginActivity.this, RegisterationActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);

        startActivityForResult(signInIntent, RC_SIGN_IN);
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
    public void googlesigninfailure(String message) {
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
        editor.putString("firstname", jsonObject.getString("first_name"));
        editor.putString("lastname", jsonObject.getString("last_name"));
        editor.putString("name", jsonObject.getString("name"));
        editor.putString("id", jsonObject.getString("id"));
        editor.putString("uid", uid);
        editor.putBoolean(Constants.fbloginkeys, true);
        editor.commit();
        startActivity(new Intent(this, TodoHomeActivity.class));
        finish();
        Toast.makeText(LoginActivity.this, "Welcome :" + jsonObject.getString("first_name"), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

