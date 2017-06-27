package DataLayer;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import DataBaseConnector.DataBaseConnect;
import Model.ImageModel;
import Model.LocalImageModel;

/**
 * Created by chhota on 07-03-2016.
 */
public class SubtractImageList {
    ArrayList<ImageModel> modelArrayList,dbWordlist;
    Context context;

    public SubtractImageList( ArrayList<ImageModel> modelArrayList,Context context){
        this.modelArrayList=modelArrayList;
        this.context=context;
        dbWordlist=new ArrayList<ImageModel>();
    }

    public  ArrayList<ImageModel> getSubtractedImageList(){
        Cursor cursor=new DataBaseConnect(context).getDbWord();
        if(cursor.getCount()!=0){
            if(cursor.moveToFirst()){
                do{
                    dbWordlist.add(new ImageModel(cursor.getInt(1)+""));
                }while(cursor.moveToNext());
            }

        }
        cursor.close();
        modelArrayList.removeAll(dbWordlist);
        return modelArrayList;
    }
}
