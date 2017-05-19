package com.app.todo.todohome.presenter;

import android.content.Context;
import android.net.Uri;

import com.app.todo.todohome.interactor.TodoHomeInteractor;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivityInterface;

import java.util.List;

public class TodoHomepresenter implements TodoHomePresenterInterface {
    Context context;
    TodoHomeActivityInterface viewInterface;
    TodoHomeInteractor interactor;

    public TodoHomepresenter(Context context, TodoHomeActivityInterface viewInterface) {
        this.context = context;
        this.viewInterface = viewInterface;
        interactor = new TodoHomeInteractor(context, this);
    }

    @Override
    public void getTodoNote(String uid) {
        interactor.getTodoNote(uid);

    }


    @Override
    public void getNoteFailure(String message) {
        viewInterface.getNoteFailure(message);

    }

    @Override
    public void showProgressDialog(String message) {
        viewInterface.showProgressDialog(message);

    }

    @Override
    public void hideProgressDialog() {
        viewInterface.hideProgressDialog();
    }

    @Override
    public void deleteTodoModelFailure(String message) {
        viewInterface.deleteTodoHomeaModelSuccess(message);
    }



    @Override
    public void getNotesListSuccess(List<TodoHomeDataModel> modelList) {
        viewInterface.getNotesListSuccess(modelList);

    }

    @Override
    public void moveToNotes(TodoHomeDataModel itemModel) {
        interactor.moveToNotes(itemModel);

    }


}
