package com.app.todo.login.interactor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.app.todo.login.presenter.LoginPresenterInterface;
import com.app.todo.registration.model.UserDatamodel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginInteractor implements LoginInteractorInterface {
    Context context;
    LoginPresenterInterface presenter;
    private DatabaseReference mfirebasedatabase;
    private FirebaseAuth auth;

    public LoginInteractor(Context context, LoginPresenterInterface presenter) {
        this.context = context;
        this.presenter = presenter;
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();


    }

    @Override
    public void getLoginResponse(String email_login, String password_login) {
        presenter.showProgressDialog("Please wait...");
        auth.signInWithEmailAndPassword(email_login, password_login).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Login Successfull", Toast.LENGTH_SHORT).show();
                    String uid = task.getResult().getUser().getUid();
                    registeruserdata(uid);
                } else {
                    Toast.makeText(context, "enter correct details", Toast.LENGTH_SHORT).show();


                }
                presenter.hideProgressDialog();
            }
        });
    }

    private void registeruserdata(final String uid) {
        mfirebasedatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserDatamodel model = snapshot.getValue(UserDatamodel.class);
                presenter.loginSuccess(model, uid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }

        });


    }

    @Override
    public void getfacebookResponse(CallbackManager callbackManager, LoginButton loginButton) {
      // presenter.showProgressDialog("please wait.....");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
               // presenter.hideProgressDialog();
            }

            @Override
            public void onCancel() {presenter.fbFailure("cancel");
            }

            @Override
            public void onError(FacebookException error) {

                presenter.fbFailure(error.toString());
                presenter.hideProgressDialog();
            }

        });
    }



    private void handleFacebookAccessToken(final AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            String uid = task.getResult().getUser().getUid();
                            try {
                                presenter.getfacebookdata(uid, object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    Bundle parameters = new Bundle();

                    parameters.putString("fields", "id, name, first_name, last_name, email");

                    request.setParameters(parameters);

                    request.executeAsync();

                }
presenter.hideProgressDialog();
            }
        });
    }

}



