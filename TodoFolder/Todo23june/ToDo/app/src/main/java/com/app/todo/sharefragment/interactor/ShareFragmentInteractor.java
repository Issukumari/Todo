package com.app.todo.sharefragment.interactor;


import android.content.Context;

import com.app.todo.R;
import com.app.todo.sharefragment.presenter.ShareFragmentPresenter;
import com.app.todo.todohome.model.TodoHomeDataModel;
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


public class ShareFragmentInteractor implements ShareFragmentInteractorInterface {
    Context context;
    ShareFragmentPresenter presenter;
    DatabaseReference mfirebasedatabase;
    FirebaseAuth auth;

    public ShareFragmentInteractor(Context context, ShareFragmentPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
        auth = FirebaseAuth.getInstance();
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference(Constants.note_details);

    }

  @Override
  public void getNoteList(String uid) {
      presenter.showDialog(context.getString(R.string.loading));
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
              presenter.getNoteListSuccess(datamodels);
              presenter.hideDialog();

          }


          @Override
          public void onCancelled(DatabaseError databaseError) {

              presenter.hideDialog();

          }
      });
      presenter.getNoteListFailure(context.getString(R.string.no_internet));
      presenter.hideDialog();
  }
}



