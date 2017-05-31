package com.app.todo.trash.presenter;

import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;

/**
 * Created by bridgeit on 24/5/17.
 */
public interface TrashPresenterInterface {
    void showDialog(String message);
    void hideDialog();
    void getDeleteNote(String uId);
    void noteDeleteSuccess(List<TodoHomeDataModel> notesModelList);
    void noteDeleteFailure(String message);
}
