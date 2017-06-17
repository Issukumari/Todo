package com.app.todo.todonoteaddactivity.presenter;

import android.os.Bundle;

import com.app.todo.todohome.model.TodoHomeDataModel;

/**
 * Created by bridgeit on 9/6/17.
 */
public interface TodoNoteAddPresenterInterface {
    void noteaddSuccess(  String message);

    void noteaddFailure(String message);

    void showProgressDialog(String mesage);

    void hideProgressDialog();

    void addNoteToFirebase(Bundle bundle);

    void CallBackNotes(TodoHomeDataModel model);
}
