package com.app.todo.todonoteaddfragment.interactor;

import android.content.Context;
import android.util.Log;

import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todonoteaddfragment.presenter.TodoNoteAddPresenterInterface;
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

public class TodoNoteAddInteractor implements TodoNoteAddInteractorInterface {
    Context context;
    TodoNoteAddPresenterInterface presenter;

    private DatabaseReference mfirebasedatabase;
    private FirebaseAuth auth;
    private TodoHomeDataModel todoHomeDataMode;
    private String uid;
    private String TAG="TodoNoteAddInteractor";

    public TodoNoteAddInteractor(Context context, TodoNoteAddPresenterInterface presenter) {
        this.context = context;
        this.presenter = presenter;
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void addNoteToFirebase(final TodoHomeDataModel todoHomeDataModel) {
//        presenter.showProgressDialog(context.getString(R.string.loading));
        uid = auth.getCurrentUser().getUid();
        todoHomeDataMode = todoHomeDataModel;
        final boolean[] flag = {true};
        //todoHomeDataMode = new TodoHomeDataModel();
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
                        //  todoHomeDataMode.setStartdate(currentDate);
                        try{
                            todoHomeDataModel.addAll(dataSnapshot.child("note_details").child(uid).child(currentDate).getValue(arrayListGenericTypeIndicator));
                            index = todoHomeDataModel.size();
                        }catch(Exception e){
                            Log.i(TAG, "onDataChange: "+e);
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
                    presenter.addNoteFailure("note add fail");
                }
            });

            presenter.addNotesSuccess("add successful");

        } catch (Exception e) {
            presenter.addNoteFailure(e.getMessage());
        }
        presenter.hideProgressDialog();
    }


    private void putdata(int index, TodoHomeDataModel todoHomeDataModel) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        String currentDate = format.format(new Date().getTime());
        todoHomeDataModel.setId(index);
        mfirebasedatabase.child("note_details").child(uid).child(currentDate).child(String.valueOf(index)).setValue(todoHomeDataModel);


    }


}

