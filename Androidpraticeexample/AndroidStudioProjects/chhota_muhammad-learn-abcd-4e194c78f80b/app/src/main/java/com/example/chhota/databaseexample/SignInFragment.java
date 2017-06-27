package com.example.chhota.databaseexample;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.EmailValidator;

import CallBackInterface.CallbackCustomMessage;
import Model.CustomMessageModel;
import Model.LoginModle;
import ProjectConstants.Constant;
import Utilities.InternetConnection;
import webservice.LoginWebService;
import com.example.chhota.customized.ListenerEditText;

/**
 * Created by chhota on 16-02-2016.
 */
public class SignInFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    ListenerEditText id, password;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;
    Button login,skip;
    boolean validemail = false;
    boolean validPassword = false;
    Activity activity;
    private ProgressDialog dialog;
    SharedPreferences sharedpreferences;
    View view;
    Context context;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        context=container.getContext();
        sharedpreferences = activity.getSharedPreferences(Constant.LoginSharedPref, Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean(Constant.IsLogin,false)){
            openHomeActivity();
        }
        else{
            view=inflater.inflate(R.layout.signin_fragment, container, false);
            initalize();
        }

        return view;
    }

    private void initalize() {

        id = (ListenerEditText) view.findViewById(R.id.loginId);
        password = (ListenerEditText) view.findViewById(R.id.password);
        login = (Button) view.findViewById(R.id.login_button);
        skip = (Button) view.findViewById(R.id.skip);
        signInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage((FragmentActivity)activity/* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new InternetConnection().checkWifiConnection(context)){
                    signIn();
                }
                else{
                    new InternetConnection().noInternetAlertDialog(context);
                }

            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeActivity();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new InternetConnection().checkWifiConnection(context)){
                    if (validemail) {
                        if (validPassword) {
                            dialog = ProgressDialog.show(activity, "Please wait...", "Loading list", true);
                            dialog.show();
                            LoginModle loninModel = new LoginModle(id.getText().toString(), password.getText().toString());
                            LoginWebService.logIn(loninModel, new CallbackCustomMessage() {
                                @Override
                                public void onSuccess(CustomMessageModel message) {
                                    dialog.dismiss();
                                    if (message.getSuccess()) {
                                        Toast.makeText(activity, "Login Success", Toast.LENGTH_LONG).show();
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putBoolean(Constant.IsLogin, true);
                                        editor.commit();
                                        openHomeActivity();
                                    } else
                                        Toast.makeText(activity, "Invalid User Name Password", Toast.LENGTH_LONG).show();

                                }

                                @Override
                                public void onError(String error) {
                                    dialog.dismiss();
                                    Toast.makeText(activity, "Fail:" + error, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            password.setError(getString(R.string.enter_valid_password));
                            password.setFocusable(true);
                        }
                    } else {
                        id.setError(getString(R.string.enter_valid_id));
                        id.setFocusable(true);
                    }
                }
                else{
                    new InternetConnection().noInternetAlertDialog(context);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!GenericValidator.isBlankOrNull(s.toString())) {
                    if (s.toString().length() < 6) {
                        String estring = "Please enter a valid email address";
                        //password.setBackgroundResource(com.android.internal.R.drawable.popup_inline_error);
                        password.setError(getString(R.string.enter_valid_password));
                        password.setFocusable(true);
                        validPassword = false;
                    } else {
                        password.setError(null);
                        validPassword = true;
                    }
                }

            }
        });

        password.setKeyImeChangeListener(new ListenerEditText.KeyImeChange() {

            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {

                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    password.clearFocus();
                    password.setError(null);
                }

            }
        });
        id.setKeyImeChangeListener(new ListenerEditText.KeyImeChange() {

            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {

                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    id.clearFocus();
                    id.setError(null);
                }


            }
        });
        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean valid = EmailValidator.getInstance().isValid(s.toString());
                if (!valid) {
                    //id.setBackgroundResource(com.android.internal.R.drawable.popup_inline_error);
                    id.setError(getString(R.string.enter_valid_id));
                    id.setFocusable(true);
                    validemail = false;
                } else {
                    id.setError(null);
                    validemail = true;
                }

            }
        });


    }

    /*
    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
           // Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            //showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                   // hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        // Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Toast.makeText(activity, "Sign In:" + acct.getDisplayName(), Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(Constant.IsLogin, true);
            editor.putString(Constant.NAME, acct.getDisplayName());
            editor.putString(Constant.EMAIL, acct.getEmail());
            editor.commit();
            openHomeActivity();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            Toast.makeText(activity, "Invalid Email Id:", Toast.LENGTH_LONG).show();

            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("GmailFailLogin", "onConnectionFailed:" + connectionResult);
    }
    private  void openHomeActivity(){
        Intent intent=new Intent(activity,HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


}
