package com.app.todo.todohome.presenter;

import android.content.Context;

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
    public void getNoteSuccess(List<TodoHomeDataModel> noteList) {
        viewInterface.getNoteSuccess(noteList);

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
    public void deleteTodoModelSuccess(String message) {
        viewInterface.deleteTodoHomeModelaFailure(message);
    }

    @Override
    public void deleteTodoModel(List<TodoHomeDataModel> tempList, TodoHomeDataModel itemModel, int position) {
        interactor.deleteTodoModel(tempList, itemModel, position);

    }


    @Override
    public void moveToArchieve(TodoHomeDataModel todohomedatamodel,int position) {
        interactor.moveToArchieve(todohomedatamodel,position);

    }

    @Override
    public void moveFailure(String message) {
        viewInterface.moveToArchieveFailure(message);

    }

    @Override
    public void moveSuccess(String message) {
        viewInterface.moveToArchieveSuccess(message);

    }

    @Override
    public void moveToNotes(TodoHomeDataModel itemModel) {
        interactor.moveToNotes(itemModel);

    }
}
