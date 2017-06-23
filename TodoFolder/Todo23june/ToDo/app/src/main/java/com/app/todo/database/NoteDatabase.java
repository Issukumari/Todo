package com.app.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.presenter.TodoHomepresenter;
import com.app.todo.todonoteaddactivity.interactor.TodoNoteAddInteractor;
import com.app.todo.todonoteaddactivity.presenter.TodoNoteAddPresenter;
import com.app.todo.todonoteaddactivity.ui.TodoNoteaddActivity;
import com.app.todo.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class NoteDatabase extends SQLiteOpenHelper {

    private SharedPreferences sharedpreferences;
    private String uid;
    TodoNoteAddInteractor todoNoteAddInteractor;
    public NoteDatabase(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    public NoteDatabase(Context context, TodoNoteAddInteractor todoNoteAddInteractor) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        this.todoNoteAddInteractor=todoNoteAddInteractor;
        sharedpreferences = context.getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        uid = sharedpreferences.getString(Constants.uid, Constants.nullvalues);


    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String Todo_Addnote_TABLE = Constants.CREATE_TABLE + Constants.TABLE_Addnote + "("
                + Constants.Title + Constants.text + Constants.Description + Constants.text + Constants.id + Constants.text +  Constants.reminderKey + Constants.text +Constants.remindertime + Constants.text+ Constants.color + Constants.textlast+")";
        sqLiteDatabase.execSQL(Todo_Addnote_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j) {
        sqLiteDatabase.execSQL(Constants.DROP_TABLE_IF_EXISTS + Constants.TABLE_Addnote);
        onCreate(sqLiteDatabase);

    }

    public void addItemtolocal(TodoHomeDataModel model) {
        SQLiteDatabase database = null;
            database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Constants.Title, model.getTitle());
            values.put(Constants.Description, model.getDescription());
            values.put(Constants.reminderKey, model.getReminderDate());
            values.put(Constants.remindertime, model.getRemindertime());
            values.put(Constants.color, model.getColor());
            values.put(Constants.id, model.getId());
            database.insert(Constants.TABLE_Addnote, null, values);
            todoNoteAddInteractor.CallBackNotes(model);//

        database.close();
        }

    public int updateItem(TodoHomeDataModel model) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.Title, model.getTitle());
        values.put(Constants.reminderKey, model.getReminderDate());
        values.put(Constants.remindertime, model.getRemindertime());
        values.put(Constants.color, model.getColor());
        values.put(Constants.Description, model.getDescription());
        values.put(Constants.id, model.getId());
        return database.update(Constants.TABLE_Addnote, values, Constants.Title + " = ?",
                new String[]{model.getTitle()});
    }


   /* public TodoHomeDataModel getNotes(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_Addnote, new String[]{Constants.Title, Constants.Description}, Constants.Title = "?", new String[]{title}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        TodoHomeDataModel model = new TodoHomeDataModel(cursor.getString(0), cursor.getString(1),cursor.getString(2));
        return model;
    }*/

    public List<TodoHomeDataModel> getNote() {
        List<TodoHomeDataModel> dataModels = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Constants.TABLE_Addnote;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                TodoHomeDataModel model = new TodoHomeDataModel();
                model.setTitle(cursor.getString(0));
                model.setDescription(cursor.getString(1));
                dataModels.add(model);
            } while (cursor.moveToNext());
        }

        return dataModels;
    }

    public void removeItem(TodoHomeDataModel model) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(Constants.TABLE_Addnote, Constants.Title + " =?", new String[]{model.getTitle()});
        database.close();
    }


}
