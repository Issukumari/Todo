package com.app.todo.notes.presenter;

import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;

/**
 * Created by bridgeit on 15/5/17.
 */
public interface NotesPresenterInterface {
    void showDialog(String message);
    void hideDialog();
    void getNoteList(String uId);
    void getNoteListSuccess(List<TodoHomeDataModel> modelList);
    void getNoteListFailure(String message);
}
