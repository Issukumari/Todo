package com.app.todo.todohome.interactor;

import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;


public interface TodoHomeInteractorInterface {
    void getTodoNote(String uid);
    void deleteTodoModel(List<TodoHomeDataModel> tempList, TodoHomeDataModel todoHomeDataModel, int pos);

    void moveToArchieve(TodoHomeDataModel todoHomeDataModel,int position);

    void moveToNotes(TodoHomeDataModel todoHomeDataModel);
}

