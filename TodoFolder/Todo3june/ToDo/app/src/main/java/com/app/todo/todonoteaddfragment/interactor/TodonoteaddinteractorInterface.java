package com.app.todo.todonoteaddfragment.interactor;

import com.app.todo.registration.model.UserDatamodel;
import com.app.todo.todohome.model.TodoHomeDataModel;

/**
 * Created by bridgeit on 6/5/17.
 */
public interface TodonoteaddinteractorInterface {
    void addNoteToFirebase(TodoHomeDataModel todoHomeDataModel);
}
