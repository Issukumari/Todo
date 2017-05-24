package com.app.todo.trash.ui;

import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;

/**
 * Created by bridgeit on 24/5/17.
 */
public interface TrashFragmentInterface {
    void showDialog(String message);
    void hideDialog();

    void deleteSuccess(List<TodoHomeDataModel> modelList);
    void deleteFailure(String message);
}
