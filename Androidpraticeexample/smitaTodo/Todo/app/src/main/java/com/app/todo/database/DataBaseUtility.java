package com.app.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.todo.model.NotesModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bridgeit on 11/4/17.
 */

public class DataBaseUtility extends SQLiteOpenHelper {

    private static final String DataBase_Name = "TodoNotes";
    private static final int DATABASE_VERSION = 1;
    private static final String Todo_Notes = "Todo_notes";
    private static final String Title_table = "title";
    private static final String Time="time";
    private static final String Content_table = "content";
    NotesModel model;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    public DataBaseUtility(Context context) {
        super(context, DataBase_Name, null, DATABASE_VERSION);

    }

    //Table Create
    @Override
    public void onCreate(SQLiteDatabase db) {

        String Todo_table = "CREATE TABLE " + Todo_Notes + "(" + Title_table + " text," + Content_table + " text," + Time+ " text"+ ")";
        db.execSQL(Todo_table);
    }
    //+ Time
    //Upgrading DB
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop older table if existed
        db.execSQL("Drop table if exists" + Todo_Notes);
        //create table again
        onCreate(db);
    }

    public void addNotes(NotesModel model ) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Title_table, model.getTitle());//notes title
        values.put(Content_table, model.getContent());//notes content
        values.put(Time,model.getDate());//date & time
        //inserting rows
        database.insert(Todo_Notes, null, values);
        database.close();

    }

    public List<NotesModel> getDatafromDB() {
        List<NotesModel> dataBaseUtilities = new ArrayList<NotesModel>();
        sqLiteDatabase = this.getWritableDatabase();
        String selectQuery="SELECT * FROM " +Todo_Notes;

        cursor =sqLiteDatabase.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()) {
            do {
                model = new NotesModel();
                model.setTitle(cursor.getString(0));
                model.setContent(cursor.getString(1));
                model.setDate(cursor.getString(2));
                dataBaseUtilities.add(model);
            } while (cursor.moveToNext());

        }

        return dataBaseUtilities;
    }

    public void delete(NotesModel notesModel) {
        sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(Todo_Notes, Title_table + " =? ", new String[]{notesModel.getTitle()});
        sqLiteDatabase.close();
    }
    /*Notes getNote(int titele) {

    return notes}*/
   /* protected void openDatabase() {
        sqLiteDatabase = openOrCreateDatabase(Todo_Notes, Context.MODE_PRIVATE, null);
    }

    protected void showRecords() {
        String title = cursor.getString(0);
        String content = cursor.getString(1);
        editTextName.setText(title);
        editTextAdd.setText(content);
    }*/
   /*public void update(NotesModel notesModel){
       sqLiteDatabase=this.getWritableDatabase();
       sqLiteDatabase.update(Todo_Notes,Title_table + " =? ",new String[]{});
   }*/
}
