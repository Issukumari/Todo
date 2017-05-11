package com.app.todo.reminderfragment.presenter;

import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;

/**
 * Created by bridgeit on 10/5/17.
 */
public interface ReminderPresenterInterface {
    void getReminderSuccess(List<TodoHomeDataModel> noteList);

    void showProgressDialog(String message);

    void getReminderFailure(String message);

    void hideProgressDialog();

    void getReminderList(String uid);
}
