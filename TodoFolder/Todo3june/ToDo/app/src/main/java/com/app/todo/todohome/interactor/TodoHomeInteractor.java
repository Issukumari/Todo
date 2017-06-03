package com.app.todo.todohome.interactor;

import android.content.Context;

import com.app.todo.R;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.presenter.TodoHomepresenter;
import com.app.todo.utils.CommonUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TodoHomeInteractor implements TodoHomeInteractorInterface {
    Context context;
    TodoHomepresenter presenter;
    DatabaseReference databaseReference;

    FirebaseAuth auth;

    public TodoHomeInteractor(Context context, TodoHomepresenter presenter) {
        this.context = context;
        this.presenter = presenter;
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.note_details));
    }

    @Override
    public void getTodoNote(final String uid) {
        String userId = auth.getCurrentUser().getUid();
        if (CommonUtils.isNetworkConnected(context)) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<TodoHomeDataModel> datamodels = new ArrayList<>();
                    GenericTypeIndicator<ArrayList<TodoHomeDataModel>> t = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
                    };
                    String uid = auth.getCurrentUser().getUid();

                    //uid = auth.getCurrentUser().getUid();
                    for (DataSnapshot post : dataSnapshot.child("note_details").child(uid).getChildren()) {
                        ArrayList<TodoHomeDataModel> data = new ArrayList<TodoHomeDataModel>();
                        data.addAll(post.getValue(t));
                        datamodels.addAll(data);
                    }
                    datamodels.removeAll(Collections.singleton(null));
                    presenter.getNotesListSuccess(
                            datamodels);
                    presenter.hideProgressDialog();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    presenter.getNoteFailure(context.getString(R.string.fail));
                    presenter.hideProgressDialog();
                }
            });
        } else {
            presenter.getNoteFailure(context.getString(R.string.no_internet));
            presenter.hideProgressDialog();
        }
    }

    @Override
    public void moveToNotes(TodoHomeDataModel todoHomeDataModel) {

    }}