package com.app.todo.todonoteaddfragment.presenter;

import android.os.Bundle;

import com.app.todo.registration.model.UserDatamodel;

/**
 * Created by bridgeit on 6/5/17.
 */
public interface TodonoteaddpresenterInterface {
    void noteaddSuccess(  String message);

    void noteaddFailure(String message);

    void showProgressDialog(String mesage);

    void hideProgressDialog();

    void addNoteToFirebase(Bundle bundle);
}
