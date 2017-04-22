package com.app.todo.registration.interactor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.registration.model.UserDatamodel;
import com.app.todo.registration.presenter.RegisterationPresenter;
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
      presenter.showProgressDialog("Please wait...");
      mfirebasedatabase= FirebaseDatabase.getInstance().getReference();
        String username = datamodel.getEmailRegistration();
        String password = datamodel.getPasswordRegistration();
        auth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            mfirebasedatabase.child(context.getString(R.string.registration)).child(task.getResult().getUser().getUid()).setValue(datamodel);
                        }else {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
