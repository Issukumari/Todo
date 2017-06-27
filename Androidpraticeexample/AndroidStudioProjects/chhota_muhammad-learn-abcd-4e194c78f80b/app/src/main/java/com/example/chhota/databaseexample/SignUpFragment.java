package com.example.chhota.databaseexample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.chhota.customized.ListenerEditText;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.EmailValidator;

import CallBackInterface.CallbackCustomMessage;
import Model.CustomMessageModel;
import Model.LoginModle;
import Model.RegisterModel;
import ProjectConstants.Constant;
import Utilities.InternetConnection;
import Utilities.StringUtilities;
import webservice.LoginWebService;

/**
 * Created by chhota on 16-02-2016.
 */
public class SignUpFragment extends Fragment{
    ListenerEditText id, password,name,MobileNo;
    Activity activity;
    Context context;
    private ProgressDialog dialog;
    SharedPreferences sharedpreferences;
    Button register;
    boolean validemail = false;
    boolean validPassword = false;
    boolean isValidMobile = false;
    boolean isValiidName = false;
    View view;
    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        context=container.getContext();
        sharedpreferences = activity.getSharedPreferences(Constant.LoginSharedPref, Context.MODE_PRIVATE);

            view=inflater.inflate(R.layout.sign_up_fragment, container, false);
            initalize();

        return view;
    }
    private void initalize() {
        activity = getActivity();
        id = (ListenerEditText) view.findViewById(R.id.loginId);
        password = (ListenerEditText) view.findViewById(R.id.password);

        name = (ListenerEditText) view.findViewById(R.id.name);
        MobileNo = (ListenerEditText) view.findViewById(R.id.mobile);

        register = (Button) view.findViewById(R.id.login_button);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new InternetConnection().checkWifiConnection(context)){
                    if (validemail) {
                        if (validPassword) {
                            if(isValiidName) {
                                if(isValidMobile) {
                                    dialog = ProgressDialog.show(activity, "Please wait...", "Loading list", true);
                                    dialog.show();
                                    RegisterModel loninModel = new RegisterModel(id.getText().toString(), password.getText().toString()
                                            ,name.getText().toString(),MobileNo.getText().toString());
                                    LoginWebService.register(loninModel, new CallbackCustomMessage() {
                                        @Override
                                        public void onSuccess(CustomMessageModel message) {
                                            dialog.dismiss();
                                            if (message.getSuccess()) {
                                                Toast.makeText(activity, "Register Success", Toast.LENGTH_LONG).show();
                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                editor.putBoolean(Constant.IsLogin, true);
                                                editor.putString(Constant.NAME,name.getText().toString());
                                                editor.putString(Constant.EMAIL, id.getText().toString());
                                                editor.commit();
                                                openHomeActivity();
                                            } else
                                                Toast.makeText(activity, "Register Error...", Toast.LENGTH_LONG).show();

                                        }

                                        @Override
                                        public void onError(String error) {
                                            dialog.dismiss();
                                            Toast.makeText(activity, "Fail:" + error, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else {
                                    MobileNo.setError(getString(R.string.enter_valid_mobileNo));
                                    MobileNo.setFocusable(true);
                                }
                            } else {
                                name.setError(getString(R.string.enter_valid_name));
                                name.setFocusable(true);
                            }
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


        MobileNo.setKeyImeChangeListener(new ListenerEditText.KeyImeChange() {

            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {

                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    MobileNo.clearFocus();
                    MobileNo.setError(null);
                }
            }
        });


        MobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean valid = StringUtilities.validateMobileNo(s.toString());
                if (!valid) {
                    //id.setBackgroundResource(com.android.internal.R.drawable.popup_inline_error);
                    MobileNo.setError(getString(R.string.enter_valid_mobileNo));
                    MobileNo.setFocusable(true);
                    isValidMobile = false;
                } else {
                    MobileNo.setError(null);
                    isValidMobile = true;
                }

            }
        });


        name.setKeyImeChangeListener(new ListenerEditText.KeyImeChange() {

            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {

                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    name.clearFocus();
                    name.setError(null);
                }


            }
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean valid = StringUtilities.validateuserName(s.toString());
                if (!valid) {
                    //id.setBackgroundResource(com.android.internal.R.drawable.popup_inline_error);
                    name.setError(getString(R.string.enter_valid_name));
                    name.setFocusable(true);
                    isValiidName = false;
                } else {
                    name.setError(null);
                    isValiidName = true;
                }

            }
        });

    }

    private  void openHomeActivity(){
        Intent intent=new Intent(activity,HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}

