
package com.app.todo.todonoteaddactivity.interactor;

import android.content.Context;
import android.util.Log;

import com.app.todo.database.NoteDatabase;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todonoteaddactivity.presenter.TodoNoteAddPresenterInterface;
import com.app.todo.utils.CommonUtils;
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
import java.util.List;
import java.util.SimpleTimeZone;

public class TodoNoteAddInteractor implements TodoNoteAddInteractorInterface {
    Context context;
    TodoNoteAddPresenterInterface presenter;

    private DatabaseReference mfirebasedatabase;
    private FirebaseAuth auth;
    private TodoHomeDataModel todoHomeDataMode;
    private String uid;
    private String TAG = "TodoNoteAddInteractor";
    private NoteDatabase database;

    public TodoNoteAddInteractor(Context context, TodoNoteAddPresenterInterface presenter) {
        this.context = context;
        this.presenter = presenter;
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void addNoteToFirebase(final TodoHomeDataModel todoHomeDataModel) {
        uid = auth.getCurrentUser().getUid();
        todoHomeDataMode = todoHomeDataModel;
        final boolean[] flag = {true};
        if (CommonUtils.isNetworkConnected(context)) {

            try {

                mfirebasedatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int index = 0;
                        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
                        String currentDate = format.format(new java.util.Date().getTime());
                        if (dataSnapshot.child("note_details").hasChild(uid)) {
                            GenericTypeIndicator<ArrayList<TodoHomeDataModel>> arrayListGenericTypeIndicator = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
                            };
                            List<TodoHomeDataModel> todoHomeDataModel = new ArrayList<>();
                            try {
                                todoHomeDataModel.addAll(dataSnapshot.child("note_details").child(uid).child(currentDate).getValue(arrayListGenericTypeIndicator));
                                index = todoHomeDataModel.size();
                            } catch (Exception e) {
                                Log.i(TAG, "onDataChange: " + e);
                            }
                        }
                        if (todoHomeDataMode != null) {
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
            presenter.hideProgressDialog();
        } /*else {
            localdatabase(todoHomeDataModel);
        }*/
    }

    @Override
    public void CallBackNotes(TodoHomeDataModel model) {
        presenter.CallBackNotes(model);
    }

    private void localdatabase(TodoHomeDataModel todoHomeDataModel) {
        presenter.showProgressDialog("loading");
        database = new NoteDatabase(context, this);
        database.addItemtolocal(todoHomeDataModel);
    }


    private void putdata(int index, TodoHomeDataModel todoHomeDataModel) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        String currentDate = format.format(new Date().getTime());
        todoHomeDataModel.setId(index);
        mfirebasedatabase.child("note_details").child(uid).child(currentDate).child(String.valueOf(index)).setValue(todoHomeDataModel);


    }


}

