
package bridgelabz.app.com.listviewswipetest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import bridgelabz.app.com.listviewswipetest.model.DataModel;

public class DatabaseActivity extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TodoHome";
    private static final String TABLE_Addnote = "Addnotes";
    private static String KEY_Title = "Title";
    private static final String KEY_Description = "Description";
    public DatabaseActivity(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String Todo_Addnote_TABLE = "CREATE TABLE " + TABLE_Addnote + "("
                    + KEY_Title + " text," + KEY_Description + " text" +")";
        sqLiteDatabase.execSQL(Todo_Addnote_TABLE);
        }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_Addnote);
        onCreate(sqLiteDatabase);

    }
  public void addItem(DataModel model)
  {
      Log.i("", "addItem: ");
      SQLiteDatabase database=this.getWritableDatabase();
      ContentValues values=new ContentValues();
      values.put(KEY_Title,model.getTitle());
      values.put(KEY_Description,model.getDescription());
      database.insert(TABLE_Addnote,null,values);
      database.close();

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
        List<DataModel> dataModels = new ArrayList<DataModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_Addnote;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DataModel model = new DataModel();
                model.setTitle(cursor.getString(0));
                model.setDescription(cursor.getString(1));
                // Adding contact to list
                dataModels.add(model);
            } while (cursor.moveToNext());
        }

        return dataModels;
    }
    public void removeItem(DataModel model)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_Addnote, KEY_Title + " = ?",
                new String[]
                        {String.valueOf(model.getTitle()),String.valueOf(model.getDescription())});
                         database.close();
    }}
