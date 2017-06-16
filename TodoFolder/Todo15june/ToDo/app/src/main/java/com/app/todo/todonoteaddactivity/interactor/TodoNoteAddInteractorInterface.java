package com.app.todo.todonoteaddactivity.interactor;

import com.app.todo.todohome.model.TodoHomeDataModel;

/**
 * Created by bridgeit on 9/6/17.
 */
public interface TodoNoteAddInteractorInterface {
    void addNoteToFirebase(TodoHomeDataModel todoHomeDataModel);

    void CallBackNotes(TodoHomeDataModel model);
}

