package com.app.todo.archievefragment.interactor;

import android.content.Context;

import com.app.todo.R;
import com.app.todo.archievefragment.presenter.ArchievepresenterInterface;
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
import java.util.List;

/**
 * Created by bridgeit on 9/5/17.
 */
public class ArchieveFragmentinteractor implements ArchieveFragmentinteractorInterface {
    Context context;
    ArchievepresenterInterface presenter;
    TodoHomeDataModel todohomedatamodel;
    private DatabaseReference mfirebasedatabase;
    private FirebaseAuth auth;
    private TodoHomeDataModel todoHomeDataMode;
    private String uid;

    public ArchieveFragmentinteractor(Context context, ArchievepresenterInterface presenter) {
        this.context = context;
        this.presenter = presenter;
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference(Constants.note_details);
    }


    public void getNoteList(final String uid) {
        presenter.showProgressDialog(context.getString(R.string.loading));
        if (CommonUtils.isNetworkConnected(context)) {

            mfirebasedatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final List<TodoHomeDataModel> noteList = new ArrayList<>();
                    GenericTypeIndicator<ArrayList<TodoHomeDataModel>> t = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
                    };
                    for (DataSnapshot obj : dataSnapshot.child(uid).getChildren()) {
                        List<TodoHomeDataModel> li;
                        li = obj.getValue(t);
                        noteList.addAll(li);
                    }
                    presenter.getNoteListSuccess(noteList);
                    presenter.hideProgressDialog();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    presenter.getNoteListFailure(context.getString(R.string.some_error));
                    presenter.hideProgressDialog();
                }

            });
        } else {
            presenter.getNoteListFailure(context.getString(R.string.no_internet));
            presenter.hideProgressDialog();
        }
    }
}


