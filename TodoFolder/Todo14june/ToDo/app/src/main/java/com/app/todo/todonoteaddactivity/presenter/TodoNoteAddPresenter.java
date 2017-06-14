package com.app.todo.todonoteaddactivity.presenter;

import android.content.Context;
import android.os.Bundle;

import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todonoteaddactivity.interactor.TodoNoteAddInteractor;
import com.app.todo.todonoteaddactivity.interactor.TodoNoteAddInteractorInterface;
import com.app.todo.todonoteaddactivity.ui.TodoNoteaddActivityInterface;
import com.app.todo.utils.Constants;

/**
 * Created by bridgeit on 9/6/17.
 */
public class TodoNoteAddPresenter implements TodoNoteAddPresenterInterface {
    Context context;
    TodoNoteaddActivityInterface viewinterface;

    TodoNoteAddInteractorInterface interactor;

    public TodoNoteAddPresenter(Context context, TodoNoteaddActivityInterface viewinterface) {
        this.context = context;
        this.viewinterface = viewinterface;

        interactor = new TodoNoteAddInteractor(context, this);
    }


    @Override
    public void noteaddSuccess(String message) {
        viewinterface.noteaddsuccess(message);
    }

    @Override
    public void noteaddFailure(String message) {
        viewinterface.noteaddFailure(message);
    }

    @Override
    public void showProgressDialog(String mesage) {
        viewinterface.showProgressDialog(mesage);

    }

    @Override
    public void hideProgressDialog() {
        viewinterface.hideProgressDialog();

    }

    @Override
    public void addNoteToFirebase(Bundle bundle) {
        TodoHomeDataModel todoHomeDataModel = new TodoHomeDataModel();
        todoHomeDataModel.setArchieve(false);
        todoHomeDataModel.setTitle(bundle.getString(Constants.Titletext));
        todoHomeDataModel.setDescription(bundle.getString(Constants.Desriptiontext));
        todoHomeDataModel.setStartdate(bundle.getString(Constants.currentdate));
        todoHomeDataModel.setReminderDate(bundle.getString(Constants.reminderKey));
        todoHomeDataModel.setRemindertime(bundle.getString(Constants.remindertime));
        todoHomeDataModel.setColor(bundle.getString(Constants.color));
        interactor.addNoteToFirebase(todoHomeDataModel);

    }
}

