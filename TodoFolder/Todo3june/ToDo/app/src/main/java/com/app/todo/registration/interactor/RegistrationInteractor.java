package com.app.todo.registration.interactor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.registration.model.UserDatamodel;
import com.app.todo.registration.presenter.RegisterationPresenter;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationInteractor implements RegistrationInteractorInterface {

    Context context;
    RegisterationPresenter presenter;
    private FirebaseAuth auth;
    private DatabaseReference mfirebasedatabase;

    public RegistrationInteractor(Context context, RegisterationPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public void getRegistrationResponse(final UserDatamodel datamodel) {
      presenter.showProgressDialog(context.getString(R.string.pleasewait));
      mfirebasedatabase= FirebaseDatabase.getInstance().getReference();
        String username = datamodel.getEmailRegistration();
        String password = datamodel.getPasswordRegistration();
        auth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            context.startActivity(new Intent(context.getApplicationContext(), TodoHomeActivity.class));

                            Toast.makeText(context, R.string.Success, Toast.LENGTH_SHORT).show();
                            mfirebasedatabase.child(context.getString(R.string.registration)).child(task.getResult().getUser().getUid()).setValue(datamodel);
                      presenter.RegistrationSuccess(datamodel,task.getResult().getUser().getUid());
                        }else {
                            Toast.makeText(context, R.string.Failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

  /*  @Override
    public void getRegistrationResponse(final String editTextEmail, final String editTextPasswordd, String editTextName, String editTextMobile, String editTextAddress) {
        presenter.showProgressDialog("Please wait...");
        mfirebasedatabase= FirebaseDatabase.getInstance().getReference();
      *//*  String username = datamodel.getEmailRegistration();
        String password = datamodel.getPasswordRegistration();*//*
        auth.createUserWithEmailAndPassword(editTextEmail,editTextPasswordd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                    mfirebasedatabase.child(context.getString(R.string.registration)).child(task.getResult().getUser().getUid()).setValue(editTextEmail,editTextPasswordd);
                }else {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/



}
