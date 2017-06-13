package com.app.todo.todohome.presenter;

import android.net.Uri;

import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;


public interface TodoHomePresenterInterface {
    void getTodoNote(String uid);

    void getNoteFailure(String message);

    void showProgressDialog(String message);

    void hideProgressDialog();


    void getNotesListSuccess(List<TodoHomeDataModel> modelList);

    void moveToNotes(TodoHomeDataModel itemModel);


}