package com.app.todo.reminderfragment.presenter;

import android.content.Context;

import com.app.todo.reminderfragment.interactor.ReminderInteractor;
import com.app.todo.reminderfragment.ui.ReminderFrragmentInterface;
import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;


public class ReminderPresenter implements ReminderPresenterInterface {
    Context context;
    ReminderFrragmentInterface viewinterface;

    ReminderInteractor interactor;

    public ReminderPresenter(Context context, ReminderFrragmentInterface viewinterface) {
        this.context = context;
        this.viewinterface = viewinterface;

        interactor = new ReminderInteractor(context, this);
    }

    @Override
    public void getReminderSuccess(List<TodoHomeDataModel> noteList) {
        viewinterface.getReminderSuccess(noteList);
    }

    @Override
    public void showProgressDialog(String message) {
        viewinterface.showProgressDialog(message);
    }

    @Override
    public void getReminderFailure(String message) {
        viewinterface.getReminderFailure(message);
    }

    @Override
    public void hideProgressDialog() {
        viewinterface.hideProgressDialog();
    }

    @Override
    public void getReminderList(String uid) {
        interactor.getReminderNotes(uid);

    }
}
