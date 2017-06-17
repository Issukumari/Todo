package com.app.todo.reminderfragment.ui;

import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;

/**
 * Created by bridgeit on 10/5/17.
 */
public interface ReminderFrragmentInterface {
    void showProgressDialog(String message);

    void hideProgressDialog();

    void getReminderSuccess(List<TodoHomeDataModel> noteList);

    void getReminderFailure(String message);
}

