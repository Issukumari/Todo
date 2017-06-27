package DataBaseConnector;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by chhota on 09-02-2016.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import ProjectConstants.Constant;

public class DataBaseConnect extends SQLiteAssetHelper {

    public DataBaseConnect(Context context){
        super(context, Constant.DATABASE_NAME,null,Constant.DATABASE_VERSION);
    }
    public Cursor getWord(String alpbate) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"ImageName", "Image"};
        String sqlTables = "AllImage";

        qb.setTables(sqlTables);
        //Cursor c = qb.query(db, sqlSelect, null, null,
                //null, null, null);
        Cursor c=db.rawQuery("select rowid _id,* from AllImage where (Type = 'word' or  Type = 'animal' or  Type = 'Fruit' or  Type = 'vehicle') AND Name like "+"'"+alpbate+"%'",null);
        //Cursor c=db.rawQuery("select rowid _id,* from AllImage",null);


        c.moveToFirst();
        return c;

    }

    public Cursor getDbWord() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"ImageName", "Image"};
        String sqlTables = "AllImage";

        qb.setTables(sqlTables);
        //Cursor c = qb.query(db, sqlSelect, null, null,
        //null, null, null);
        Cursor c=db.rawQuery("select rowid _id,ExternalId from AllImage",null);


        c.moveToFirst();
        return c;

    }

    public Cursor getAlphbate(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sqlTables = "AllImage";

        qb.setTables(sqlTables);
        //Cursor c = qb.query(db, sqlSelect, null, null,
        //null, null, null);
        Cursor c=db.rawQuery("select rowid _id,* from AllImage where Type = 'alphabate'",null);


        c.moveToFirst();
        return c;
    }

    public Cursor geShape(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sqlTables = "AllImage";

        qb.setTables(sqlTables);
        Cursor c=db.rawQuery("select rowid _id,* from AllImage where Type = 'shape' ",null);
        c.moveToFirst();
        return c;
    }


    public Cursor getColor(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sqlTables = "AllImage";

        qb.setTables(sqlTables);
        Cursor c=db.rawQuery("select rowid _id,* from AllImage where Type = 'color' ",null);
        c.moveToFirst();
        return c;
    }


    public Cursor getFruits(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sqlTables = "AllImage";

        qb.setTables(sqlTables);
        Cursor c=db.rawQuery("select rowid _id,* from AllImage where Type = 'Fruit' ",null);
        c.moveToFirst();
        return c;
    }


    public Cursor getVehicles(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sqlTables = "AllImage";

        qb.setTables(sqlTables);
        Cursor c=db.rawQuery("select rowid _id,* from AllImage where Type = 'vehicle' ",null);
        c.moveToFirst();
        return c;
    }


    public Cursor getAnimals(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String sqlTables = "AllImage";

        qb.setTables(sqlTables);
        Cursor c=db.rawQuery("select rowid _id,* from AllImage where Type = 'animal' ",null);
        c.moveToFirst();
        return c;
    }
}
