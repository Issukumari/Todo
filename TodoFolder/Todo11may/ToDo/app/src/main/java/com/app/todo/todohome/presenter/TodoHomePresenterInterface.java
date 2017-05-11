package com.app.todo.todohome.presenter;

import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;


public interface TodoHomePresenterInterface {
    void getTodoNote(String uid);
    void getNoteSuccess(List<TodoHomeDataModel> noteList);
    void getNoteFailure(String message);
    void showProgressDialog(String message);
    void hideProgressDialog();

    void deleteTodoModelFailure(String message);

    void deleteTodoModelSuccess(String message);

    void deleteTodoModel(List<TodoHomeDataModel> tempList, TodoHomeDataModel itemModel, int pos);

    void moveToArchieve(TodoHomeDataModel todoHomeDataModel,int position);

    void moveFailure(String message);

    void moveSuccess(String message);

    void moveToNotes(TodoHomeDataModel itemModel);
}
