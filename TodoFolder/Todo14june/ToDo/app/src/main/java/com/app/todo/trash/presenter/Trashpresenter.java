package com.app.todo.trash.presenter;

import android.content.Context;

import com.app.todo.notes.interactor.Notesinteractor;
import com.app.todo.notes.ui.NotesFragmentInterface;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.trash.interactor.TrashInteractor;
import com.app.todo.trash.ui.TrashFragmentInterface;

import java.util.List;

/**
 * Created by bridgeit on 24/5/17.
 */
public class Trashpresenter implements TrashPresenterInterface {
    Context context;
    TrashFragmentInterface viewInterface;
    TrashInteractor interactor;

    public Trashpresenter(Context context, TrashFragmentInterface viewInterface) {
        this.context = context;
        this.viewInterface = viewInterface;
        interactor = new TrashInteractor(context, this);
    }

    @Override
    public void showDialog(String message) {

    }

    @Override
    public void hideDialog() {

    }

    @Override
    public void getDeleteNote(String uId) {
        interactor.getDeleteNote(uId);

    }

    @Override
    public void noteDeleteSuccess(List<TodoHomeDataModel> notesModelList) {
        viewInterface.deleteSuccess(notesModelList);

    }

    @Override
    public void noteDeleteFailure(String message) {
        viewInterface.deleteFailure(message);

    }


}
