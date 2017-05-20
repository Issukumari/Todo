package com.app.todo.todohome.interactor;

import android.net.Uri;

import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;


public interface TodoHomeInteractorInterface {
    void getTodoNote(String uid);
    void moveToNotes(TodoHomeDataModel todoHomeDataModel);

}

