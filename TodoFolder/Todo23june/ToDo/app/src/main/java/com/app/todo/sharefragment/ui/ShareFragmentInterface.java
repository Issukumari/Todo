package com.app.todo.sharefragment.ui;


import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;

public interface ShareFragmentInterface {
    void showDialog(String message);

    void hideDialog();

    void getNoteListSuccess(List<TodoHomeDataModel> modelList);
    void getNotesListFailure(String message);
}