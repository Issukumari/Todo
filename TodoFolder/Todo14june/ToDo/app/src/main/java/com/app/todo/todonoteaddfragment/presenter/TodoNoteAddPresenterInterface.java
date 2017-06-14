package com.app.todo.todonoteaddfragment.presenter;

import android.os.Bundle;

import com.app.todo.registration.model.UserDatamodel;

public interface TodoNoteAddPresenterInterface {

    void addNotesSuccess(String message);

    void addNoteFailure(String message);

    void showProgressDialog(String mesage);

    void hideProgressDialog();

    void addNoteToFirebase(Bundle bundle);
}
