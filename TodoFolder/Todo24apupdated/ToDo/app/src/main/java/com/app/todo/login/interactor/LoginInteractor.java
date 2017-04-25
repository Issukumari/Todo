package com.app.todo.login.interactor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.app.todo.registration.model.UserDatamodel;
import com.app.todo.login.presenter.LoginPresenterInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginInteractor implements LoginInteractorInterface {
    Context context;
    LoginPresenterInterface presenter;
    private DatabaseReference mfirebasedatabase;
    private FirebaseAuth auth;


    public LoginInteractor(Context context, LoginPresenterInterface presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    @Override
    public void getLoginResponse(String email_login, String password_login) {
        presenter.showProgressDialog("Please wait...");
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
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
                presenter.loginSuccess(model,uid);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }

        });


    }
}
