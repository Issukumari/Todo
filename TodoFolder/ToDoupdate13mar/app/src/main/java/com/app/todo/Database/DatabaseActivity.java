package com.app.todo.Database;

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

public class DatabaseActivity extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TodoHome";
    private static final String TABLE_Addnote = "Addnotes";
    private static final String KEY_Description = "Description";
    private static String KEY_Title = "Title";

    public DatabaseActivity(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String Todo_Addnote_TABLE = "CREATE TABLE " + TABLE_Addnote + "("
                + KEY_Title + " text," + KEY_Description + " text" + ")";
        sqLiteDatabase.execSQL(Todo_Addnote_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_Addnote);
        onCreate(sqLiteDatabase);

    }

    public void addItem(DataModel model) {
        Log.i("", "addItem: ");
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_Title, model.getTitle());
        values.put(KEY_Description, model.getDescription());
        database.insert(TABLE_Addnote, null, values);
        database.close();

    }
    public int updateItem(DataModel model)
    {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_Title, model.getTitle());
        values.put(KEY_Description, model.getDescription());
        return database.update(TABLE_Addnote, values, KEY_Title + " = ?",
                new String[] { model.getTitle() });
    }


    public DataModel getNotes(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_Addnote, new String[]{KEY_Title, KEY_Description}, KEY_Title = "?", new String[]{title}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        DataModel model = new DataModel(cursor.getString(0), cursor.getString(1));
        return model;
    }

    public List<DataModel> getNote() {
        List<DataModel> dataModels = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_Addnote;
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
        database.delete(TABLE_Addnote, KEY_Title + " =?", new String[]{model.getTitle()});
        database.close();
    }
}
