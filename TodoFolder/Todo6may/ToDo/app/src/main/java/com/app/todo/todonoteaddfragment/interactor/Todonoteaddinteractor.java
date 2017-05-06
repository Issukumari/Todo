package com.app.todo.todonoteaddfragment.interactor;

import android.content.Context;

import com.app.todo.R;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todonoteaddfragment.presenter.TodonoteaddpresenterInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Todonoteaddinteractor implements TodonoteaddinteractorInterface {
    Context context;
    TodonoteaddpresenterInterface presenter;

    private DatabaseReference mfirebasedatabase;
    private FirebaseAuth auth;
    private TodoHomeDataModel todoHomeDataMode;
    private String uid;

    public Todonoteaddinteractor(Context context, TodonoteaddpresenterInterface presenter) {
        this.context = context;
        this.presenter = presenter;
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void addNoteToFirebase(final TodoHomeDataModel todoHomeDataModel) {
        presenter.showProgressDialog(context.getString(R.string.loading));
        uid = auth.getCurrentUser().getUid();
        todoHomeDataMode = todoHomeDataModel;
        final boolean[] flag = {true};
        final GenericTypeIndicator<ArrayList<TodoHomeDataModel>> typeIndicator = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
        };
        try {

            mfirebasedatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int index = 0;
                    ArrayList<TodoHomeDataModel> todoHomeDataModels_ArrayList = new ArrayList<TodoHomeDataModel>();
                    if (dataSnapshot.child("note_details").hasChild(uid)) {
                        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
                        String currentDate = format.format(new java.util.Date().getTime());
                        todoHomeDataModels_ArrayList.addAll(dataSnapshot.child("note_details").child(uid)
                                .child(currentDate)
                                .getValue(typeIndicator));
                    }
                    index = todoHomeDataModels_ArrayList.size();
                    if (todoHomeDataMode != null) {
                        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
                        String currentDate = format.format(new Date().getTime());
                        todoHomeDataMode.setStartdate(currentDate);
                        if (dataSnapshot.child("note_details").child(uid).child(currentDate).exists()) {
                            putdata(index, todoHomeDataModel);
                            todoHomeDataMode = null;
                        } else {
                            putdata(0, todoHomeDataMode);
                            todoHomeDataMode = null;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    presenter.noteaddFailure("note add fail");
                }
            });

            presenter.noteaddSuccess("add successful");

        } catch (Exception e) {
            presenter.noteaddFailure(e.getMessage());
        }
    }

    private void putdata(int index, TodoHomeDataModel todoHomeDataModel) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        String currentDate = format.format(new Date().getTime());
        todoHomeDataModel.setId(index);
        mfirebasedatabase.child("note_details").child(uid).child(currentDate).child(String.valueOf(index)).setValue(todoHomeDataModel);


    }


    }


