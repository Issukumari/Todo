package com.app.todo.archievefragment.presenter;

import android.content.Context;

import com.app.todo.archievefragment.interactor.ArchieveFragmentinteractor;
import com.app.todo.archievefragment.interactor.ArchieveFragmentinteractorInterface;
import com.app.todo.archievefragment.ui.ArchieveFragmentInterface;
import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;


public class ArchievePresenter implements  ArchievepresenterInterface {
    Context context;
    ArchieveFragmentInterface viewinterface;

    ArchieveFragmentinteractor interactor;

    public ArchievePresenter(Context context, ArchieveFragmentInterface viewinterface) {
        this.context = context;
        this.viewinterface = viewinterface;

        interactor = new ArchieveFragmentinteractor(context, this);
    }

    @Override
    public void getNoteList(String uid) {
        interactor.getNoteList(uid);

    }

    @Override
    public void getNoteListSuccess(List<TodoHomeDataModel> todoHomeDataModelList) {
        viewinterface.getNoteListSuccess(todoHomeDataModelList);

    }

    @Override
    public void getNoteListFailure(String message) {
viewinterface.getNoteListFailure(message);
    }

    @Override
    public void showProgressDialog(String message) {
        viewinterface.showProgressDialog(message);
    }

    @Override
    public void hideProgressDialog() {
        viewinterface.hideProgressDialog();
    }


}


