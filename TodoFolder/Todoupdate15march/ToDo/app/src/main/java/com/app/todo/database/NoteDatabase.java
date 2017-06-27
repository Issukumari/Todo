package com.app.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.todo.model.DataModel;
import com.app.todo.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class NoteDatabase extends SQLiteOpenHelper {

    public NoteDatabase(Context context) {
        super(context, Constants.DATABASE_NAME, null,Constants.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String Todo_Addnote_TABLE = "CREATE TABLE " + Constants.TABLE_Addnote + "("
                + Constants.Title + " text," + Constants.Description + " text" + ")";
        sqLiteDatabase.execSQL(Todo_Addnote_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_Addnote);
        onCreate(sqLiteDatabase);

    }

    public void addItem(DataModel model) {
        Log.i("", "addItem: ");
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.Title, model.getTitle());
        values.put(Constants.Description, model.getDescription());
        database.insert(Constants.TABLE_Addnote, null, values);
        database.close();

    }

    public int updateItem(DataModel model) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.Title, model.getTitle());
        values.put(Constants.Description, model.getDescription());
        return database.update(Constants.TABLE_Addnote, values, Constants.Title + " = ?",
                new String[]{model.getTitle()});
    }


    public DataModel getNotes(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_Addnote, new String[]{Constants.Title, Constants.Description}, Constants.Title = "?", new String[]{title}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        DataModel model = new DataModel(cursor.getString(0), cursor.getString(1));
        return model;
    }

    public List<DataModel> getNote() {
        List<DataModel> dataModels = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " +Constants.TABLE_Addnote;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DataModel model = new DataModel();
                model.setTitle(cursor.getString(0));
                model.setDescription(cursor.getString(1));
                dataModels.add(model);
            } while (cursor.moveToNext());
        }

        return dataModels;
    }

    public void removeItem(DataModel model) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(Constants.TABLE_Addnote, Constants.Title + " =?", new String[]{model.getTitle()});
        database.close();
    }
}
