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

/**
 * Created by bridgeit on 9/5/17.
 */
public class TodoHomeInteractor implements TodoHomeInteractorInterface {
    Context context;
    TodoHomepresenter presenter;
    DatabaseReference databaseReference;

    FirebaseAuth firebaseAuth;

    public TodoHomeInteractor(Context context, TodoHomepresenter presenter) {
        this.context = context;
        this.presenter = presenter;
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(context.getString(R.string.note_details));
    }

    @Override
    public void getTodoNote(final String uid) {
        //  presenter.showProgressDialog(context.getString(R.string.loading));
        String userId = firebaseAuth.getCurrentUser().getUid();
        /*if (CommonUtils.isNetworkConnected(context)) {
            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<TodoHomeDataModel>> arrayListGenericTypeIndicator = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
                    };
                    ArrayList<TodoHomeDataModel> todoHomeDataModels = new ArrayList<>();

                    for (DataSnapshot post : dataSnapshot.getChildren()) {

                        ArrayList<TodoHomeDataModel> notesModel_ArrayList;
                        notesModel_ArrayList = post.getValue(arrayListGenericTypeIndicator);
                        todoHomeDataModels.addAll(notesModel_ArrayList);

                    }
                    todoHomeDataModels.removeAll(Collections.singleton(null));
                    presenter.getNotesListSuccess(todoHomeDataModels);
                    presenter.hideProgressDialog();
                }*/
        if (CommonUtils.isNetworkConnected(context)) {

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final ArrayList<TodoHomeDataModel> noteList = new ArrayList<>();
                    GenericTypeIndicator<ArrayList<TodoHomeDataModel>> t = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
                    };
                    String uid = firebaseAuth.getCurrentUser().getUid();

                    for (DataSnapshot obj : dataSnapshot.child(uid).getChildren()) {
                        List<TodoHomeDataModel> li = new ArrayList<TodoHomeDataModel>();
                        li.addAll(obj.getValue(t));
                        noteList.addAll(li);
                    }
                    noteList.removeAll(Collections.singleton(null));
                    presenter.getNotesListSuccess(
                            noteList);
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

    }

}
