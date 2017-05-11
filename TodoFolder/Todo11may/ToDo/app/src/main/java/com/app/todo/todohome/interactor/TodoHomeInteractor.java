package com.app.todo.todohome.interactor;

import android.content.Context;

import com.app.todo.R;
import com.app.todo.database.NoteDatabase;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.presenter.TodoHomepresenter;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by bridgeit on 9/5/17.
 */
public class TodoHomeInteractor implements TodoHomeInteractorInterface {
    Context context;
    TodoHomepresenter presenter;

    DatabaseReference mfirebasedatabaseref;
    FirebaseDatabase mfirebaseDatabase;
    List<TodoHomeDataModel> datamodels = new ArrayList<>();
    NoteDatabase databse;
    private List<TodoHomeDataModel> allnotes;
    String uId;
    public TodoHomeInteractor(Context context, TodoHomepresenter presenter) {
        this.context = context;
        this.presenter = presenter;
        mfirebasedatabaseref = FirebaseDatabase.getInstance().getReference(Constants.note_details);
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    @Override
    public void getTodoNote(final String uid) {
        presenter.showProgressDialog(context.getString(R.string.loading));

        if (CommonUtils.isNetworkConnected(context)) {

            mfirebasedatabaseref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final List<TodoHomeDataModel> noteList = new ArrayList<>();
                    GenericTypeIndicator<ArrayList<TodoHomeDataModel>> t = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
                    };

                    for (DataSnapshot post : dataSnapshot.child(context.getString(R.string.note_details)).child(uId).getChildren()) {
                        ArrayList<TodoHomeDataModel> data = new ArrayList<TodoHomeDataModel>();
                        data.addAll(post.getValue(t));
                        noteList.addAll(data);
                     }
                    noteList.removeAll(Collections.singleton(null));
                    presenter.getNoteSuccess(noteList);
                    presenter.hideProgressDialog();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    presenter.getNoteFailure(context.getString(R.string.some_error));
                    presenter.hideProgressDialog();
                }
            });
        } else {
            presenter.getNoteFailure(context.getString(R.string.no_internet));
            presenter.hideProgressDialog();
        }
    }


    @Override
    public void deleteTodoModel(List<TodoHomeDataModel> tempList, TodoHomeDataModel todoHomeDataModel, int position) {
        presenter.showProgressDialog(context.getString(R.string.loading));
        // databse = new NoteDatabase(getApplicationContext());
        //  todoHomeDataModel = datamodels.get(position);
        if (CommonUtils.isNetworkConnected(context)) {
            mfirebasedatabaseref.child("note_details").child(uId).child(todoHomeDataModel.getStartdate())
                    .child(String.valueOf(todoHomeDataModel.getId())).removeValue();
            databse.removeItem(datamodels.get(position));

        }
    }

    @Override
    public void moveToArchieve(TodoHomeDataModel todoHomeDataModel, int position) {
        if (CommonUtils.isNetworkConnected(context)) {
            todoHomeDataModel = allnotes.get(position);
            if (todoHomeDataModel.isArchieve())
                todoHomeDataModel.setArchieve(false);
            else {
                todoHomeDataModel.setArchieve(true);

            }
            // if (CommonUtils.isNetworkConnected(TodoHomeActivity.this)) {
            mfirebasedatabaseref.child("note_details").child(uId).child(todoHomeDataModel.getStartdate())
                    .child(String.valueOf(todoHomeDataModel.getId())).setValue(todoHomeDataModel);

        } /*else {
            final TodoHomeDataModel finalTodoHomeDataModel = todoHomeDataModel;
            finalTodoHomeDataModel.setArchieve(false);
            mfirebasedatabaseref.child("note_details").child(uid).child(finalTodoHomeDataModel.getStartdate())
                    .child(String.valueOf(finalTodoHomeDataModel.getId())).setValue(finalTodoHomeDataModel);
        }*/
    }


    @Override
    public void moveToNotes(TodoHomeDataModel todoHomeDataModel) {
        presenter.moveToNotes(todoHomeDataModel);
    }
}
