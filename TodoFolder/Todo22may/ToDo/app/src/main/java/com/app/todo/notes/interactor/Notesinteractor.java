package com.app.todo.notes.interactor;

import android.content.Context;

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
import java.util.List;

/**
 * Created by bridgeit on 15/5/17.
 */
public class Notesinteractor implements NotesInteractorInterface {

    Context context;
    NotesPresenter presenter;
    DatabaseReference mfirebasedatabase;
    FirebaseAuth auth;

    public Notesinteractor(Context context, NotesPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
        auth = FirebaseAuth.getInstance();
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference(Constants.note_details);
    }


    @Override
    public void getNoteList(String uid) {
        presenter.showDialog(context.getString(R.string.loading));
      //  String userId = auth.getCurrentUser().getUid();
        if (CommonUtils.isNetworkConnected(context)) {

           /* mfirebasedatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final List<TodoHomeDataModel> noteList = new ArrayList<>();
                    GenericTypeIndicator<ArrayList<TodoHomeDataModel>> t = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
                    };
                    String uid = firebaseAuth.getCurrentUser().getUid();

                    for (DataSnapshot obj : dataSnapshot.child(uid).getChildren()) {
                        List<TodoHomeDataModel> li = new ArrayList<TodoHomeDataModel>();
                        li.addAll(obj.getValue(t));
                        noteList.addAll(li);
                    }
                    noteList.removeAll(Collections.singleton(null));*/


            mfirebasedatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<TodoHomeDataModel> datamodels = new ArrayList<>();
                    GenericTypeIndicator<ArrayList<TodoHomeDataModel>> t = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
                    };
                    String uid = auth.getCurrentUser().getUid();

                    //uid = auth.getCurrentUser().getUid();
                    for (DataSnapshot post : dataSnapshot.child(uid).getChildren()) {
                        List<TodoHomeDataModel> data = new ArrayList<>();
                        data.addAll(post.getValue(t));
                        datamodels.addAll(data);
                    }
                    datamodels.removeAll(Collections.singleton(null));
                    presenter.getNoteListSuccess(datamodels);
                    presenter.hideDialog();

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                    presenter.hideDialog();

                }
            });

        } else {
            presenter.getNoteListFailure(context.getString(R.string.no_internet));
            presenter.hideDialog();
        }
    }

}

