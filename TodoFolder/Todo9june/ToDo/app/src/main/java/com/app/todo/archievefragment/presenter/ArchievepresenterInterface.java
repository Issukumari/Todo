package com.app.todo.archievefragment.presenter;

import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;

/**
 * Created by bridgeit on 9/5/17.
 */
public interface ArchievepresenterInterface {
    void getNoteList(String uid);

    void getNoteListSuccess(List<TodoHomeDataModel> todoHomeDataModelList);

    void getNoteListFailure(String message);

    void showProgressDialog(String message);

    void hideProgressDialog();
}
