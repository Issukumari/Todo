package com.app.todo.sharefragment.presenter;

import android.content.Context;

import com.app.todo.sharefragment.interactor.ShareFragmentInteractor;
import com.app.todo.sharefragment.ui.ShareFragmentInterface;
import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;

public class ShareFragmentPresenter implements ShareFragmentPresenterInterface {

    Context context;
    ShareFragmentInterface viewInterface;
    ShareFragmentInteractor interactor;

    public ShareFragmentPresenter(Context context, ShareFragmentInterface viewInterface) {
        this.context = context;
        this.viewInterface = viewInterface;
        interactor = new ShareFragmentInteractor(context, this);
    }

    @Override
    public void showDialog(String message) {
        viewInterface.showDialog(message);
    }

    @Override
    public void hideDialog() {
        viewInterface.hideDialog();
    }

    @Override
    public void getNoteList(String uId) {
        interactor.getNoteList(uId);
    }



    @Override
    public void getNoteListSuccess(List<TodoHomeDataModel> modelList) {
        viewInterface.getNoteListSuccess(modelList);
    }

    @Override
    public void getNoteListFailure(String message) {
        viewInterface.getNotesListFailure(message);
    }
}