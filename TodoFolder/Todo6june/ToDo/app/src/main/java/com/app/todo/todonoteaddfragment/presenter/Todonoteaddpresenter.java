package com.app.todo.todonoteaddfragment.presenter;

import android.content.Context;
import android.os.Bundle;

import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todonoteaddfragment.interactor.Todonoteaddinteractor;
import com.app.todo.todonoteaddfragment.interactor.TodonoteaddinteractorInterface;
import com.app.todo.todonoteaddfragment.ui.TodonoteaddFragmentInterface;
import com.app.todo.utils.Constants;

/**
 * Created by bridgeit on 6/5/17.
 */
public class Todonoteaddpresenter implements TodonoteaddpresenterInterface {
    Context context;
    TodonoteaddFragmentInterface viewinterface;

    TodonoteaddinteractorInterface interactor;

    public Todonoteaddpresenter(Context context, TodonoteaddFragmentInterface viewinterface) {
        this.context = context;
        this.viewinterface = viewinterface;

        interactor = new Todonoteaddinteractor(context, this);
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
        todoHomeDataModel.setColor(bundle.getString(Constants.color));
        interactor.addNoteToFirebase(todoHomeDataModel);

    }
}
