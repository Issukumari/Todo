package com.app.todo.notes.presenter;

import android.content.Context;

import com.app.todo.notes.interactor.Notesinteractor;
import com.app.todo.notes.ui.NotesFragmentInterface;
import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;


public class NotesPresenter implements NotesPresenterInterface {
    Context context;
    NotesFragmentInterface viewInterface;
    Notesinteractor interactor;

    public NotesPresenter(Context context, NotesFragmentInterface viewInterface) {
        this.context = context;
        this.viewInterface = viewInterface;
        interactor = new Notesinteractor(context, this);
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

    public void updateSrNo(List<TodoHomeDataModel> datamodels) {
       // interactor.updateSrNo(datamodels);
    }
}