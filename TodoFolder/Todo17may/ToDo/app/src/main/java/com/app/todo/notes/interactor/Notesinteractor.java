package com.app.todo.notes.interactor;

import android.content.Context;
import android.util.Log;

import com.app.todo.R;
import com.app.todo.notes.presenter.NotesPresenter;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.utils.CommonUtils;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by bridgeit on 15/5/17.
 */
public class Notesinteractor implements NotesInteractorInterface {

    Context context;
    NotesPresenter presenter;
    DatabaseReference mfirebasedatabase;
    FirebaseAuth firebaseAuth;

    public Notesinteractor(Context context, NotesPresenter presenter) {
        this.context=context;
        this.presenter=presenter;
        firebaseAuth= FirebaseAuth.getInstance();
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference(Constants.note_details);

        //databaseReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.note_details));
    }


    @Override
    public void getNoteList(String uid) {
        presenter.showDialog(context.getString(R.string.loading));
        String userId = firebaseAuth.getCurrentUser().getUid();
        if (CommonUtils.isNetworkConnected(context)) {
            mfirebasedatabase.child(userId).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<TodoHomeDataModel>> arrayListGenericTypeIndicator = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
                    };
                    ArrayList<TodoHomeDataModel> todoHomeDataModel = new ArrayList<>();

                    for (DataSnapshot post : dataSnapshot.getChildren()) {

                        ArrayList<TodoHomeDataModel> todoHomeDataModelArrayList;
                        todoHomeDataModelArrayList = post.getValue(arrayListGenericTypeIndicator);
                        todoHomeDataModel.addAll(todoHomeDataModelArrayList);
                    }

                    todoHomeDataModel.removeAll(Collections.singleton(null));
                    Log.i("dd", "ondatachange:  "+todoHomeDataModel.size());

                    presenter.getNoteListSuccess(todoHomeDataModel);
                    presenter.hideDialog();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    presenter.hideDialog();

                }
            });

        }else {
            presenter.getNoteListFailure(context.getString(R.string.no_internet));
            presenter.hideDialog();
        }
    }

}

