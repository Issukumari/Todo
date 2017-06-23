package com.app.todo.sharefragment.presenter;

import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;

/**
 * Created by bridgeit on 22/6/17.
 */
public interface ShareFragmentPresenterInterface {
    void showDialog(String message);
    void hideDialog();
    void getNoteList(String uId);
    void getNoteListSuccess(List<TodoHomeDataModel> modelList);
    void getNoteListFailure(String message);
}
