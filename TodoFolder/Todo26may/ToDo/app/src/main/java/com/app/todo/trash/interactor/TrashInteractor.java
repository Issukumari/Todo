package com.app.todo.trash.interactor;

import android.content.Context;

import com.app.todo.R;
import com.app.todo.database.NoteDatabase;
import com.app.todo.notes.presenter.NotesPresenter;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.trash.presenter.Trashpresenter;
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
 * Created by bridgeit on 24/5/17.
 */
public class TrashInteractor implements  TrashInteractorInterface {
    Context context;
    Trashpresenter presenter;
    DatabaseReference mfirebasedatabase;
    FirebaseAuth auth;

    public TrashInteractor(Context context, Trashpresenter presenter) {
        this.context = context;
        this.presenter = presenter;
        auth = FirebaseAuth.getInstance();
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference(Constants.note_details);
    }

    @Override
    public void getDeleteNote(String uId) {
        presenter.showDialog(context.getString(R.string.getting_deleted_notes));

        if (CommonUtils.isNetworkConnected(context)){

          /*  mfirebasedatabase.child(uId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<TodoHomeDataModel>> arrayListGenericTypeIndicator = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
                    };
                    ArrayList<TodoHomeDataModel> todoHomeDataModels = new ArrayList<>();

                    for (DataSnapshot post : dataSnapshot.getChildren()) {

                        ArrayList<TodoHomeDataModel> notesModel_ArrayList;
                        notesModel_ArrayList = post.getValue(arrayListGenericTypeIndicator);
                        todoHomeDataModels.addAll(notesModel_ArrayList);
*/

            mfirebasedatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<TodoHomeDataModel> datamodels = new ArrayList<>();
                    GenericTypeIndicator<ArrayList<TodoHomeDataModel>> t = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
                    };
                    String uid = auth.getCurrentUser().getUid();
                    for (DataSnapshot post : dataSnapshot.child(uid).getChildren()) {
                        List<TodoHomeDataModel> data = new ArrayList<>();
                        data.addAll(post.getValue(t));
                        datamodels.addAll(data);
                    }
                    datamodels.removeAll(Collections.singleton(null));
                    presenter.noteDeleteSuccess(datamodels);
                    presenter.hideDialog();
                    }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    presenter.noteDeleteFailure(context.getString(R.string.delete_failure));
                    presenter.hideDialog();

                }
            });

        }else {
            presenter.noteDeleteFailure(context.getString(R.string.fail));
            presenter.hideDialog();
        }


    }

}

