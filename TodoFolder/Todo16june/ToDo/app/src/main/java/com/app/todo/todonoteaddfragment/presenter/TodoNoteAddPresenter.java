package com.app.todo.todonoteaddfragment.presenter;

import android.content.Context;
import android.os.Bundle;

import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todonoteaddfragment.interactor.TodoNoteAddInteractor;
import com.app.todo.todonoteaddfragment.interactor.TodoNoteAddInteractorInterface;
import com.app.todo.todonoteaddfragment.ui.TodoNoteAddFragmentInterface;
import com.app.todo.utils.Constants;

public class TodoNoteAddPresenter implements TodoNoteAddPresenterInterface {
    Context context;
    TodoNoteAddFragmentInterface viewInterface;
    TodoNoteAddInteractorInterface interactor;

    public TodoNoteAddPresenter(Context context, TodoNoteAddFragmentInterface viewInterface) {
        this.context = context;
        this.viewInterface = viewInterface;
        interactor = new TodoNoteAddInteractor(context, this);
    }


    @Override
    public void addNotesSuccess(String message) {
        viewInterface.noteaddsuccess(message);
    }

    @Override
    public void addNoteFailure(String message) {
        viewInterface.noteaddFailure(message);
    }

    @Override
    public void showProgressDialog(String mesage) {
        viewInterface.showProgressDialog(mesage);

    }

    @Override
    public void hideProgressDialog() {
        viewInterface.hideProgressDialog();
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
