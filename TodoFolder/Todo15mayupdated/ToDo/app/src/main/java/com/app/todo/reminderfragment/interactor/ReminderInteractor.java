package com.app.todo.reminderfragment.interactor;

import android.app.ProgressDialog;
import android.content.Context;

import com.app.todo.R;
import com.app.todo.reminderfragment.presenter.ReminderPresenterInterface;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.utils.CommonUtils;
import com.app.todo.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class ReminderInteractor implements Reminderinteractorinterface {
    Context context;
    ReminderPresenterInterface presenter;
    ProgressDialog progressDialog;
    private DatabaseReference mfirebasedatabase;

    public ReminderInteractor(Context context, ReminderPresenterInterface presenter) {
        this.context = context;
        this.presenter = presenter;
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference(Constants.note_details);
    }


    @Override
    public void getReminderNotes(String uid) {
        presenter.showProgressDialog(context.getString(R.string.loading));
        if (CommonUtils.isNetworkConnected(context)) {
            mfirebasedatabase.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<TodoHomeDataModel> noteList = new ArrayList<>();
                    GenericTypeIndicator<ArrayList<TodoHomeDataModel>> t = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
                    };
                    for (DataSnapshot obj :    dataSnapshot.getChildren()) {
                        List<TodoHomeDataModel> li=new ArrayList<TodoHomeDataModel>();
                        li.addAll(obj.getValue(t));
                        noteList.addAll(li);
                    }
                    noteList.removeAll(Collections.singleton(null));
                    presenter.getReminderSuccess(noteList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    presenter.getReminderFailure(context.getString(R.string.some_error));
                }
            });
        } else {
            presenter.getReminderFailure(context.getString(R.string.no_internet));
        }
        presenter.hideProgressDialog();
    }
}


