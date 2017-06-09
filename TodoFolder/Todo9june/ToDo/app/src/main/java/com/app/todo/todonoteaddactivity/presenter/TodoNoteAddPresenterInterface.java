package com.app.todo.todonoteaddactivity.presenter;

import android.os.Bundle;

/**
 * Created by bridgeit on 9/6/17.
 */
public interface TodoNoteAddPresenterInterface {
    void noteaddSuccess(  String message);

    void noteaddFailure(String message);

    void showProgressDialog(String mesage);

    void hideProgressDialog();

    void addNoteToFirebase(Bundle bundle);
}
