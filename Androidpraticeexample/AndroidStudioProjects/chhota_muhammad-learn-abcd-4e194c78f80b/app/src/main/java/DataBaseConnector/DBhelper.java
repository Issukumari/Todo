package DataBaseConnector;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.widget.Toast;
import CallBackInterface.CallbackDownloadImage;
import Model.ImageModel;
import ProjectConstants.Constant;
import webservice.ListGetWebService;

/**
 * Created by chhota on 11-02-2016.
 */
public class DBhelper extends SQLiteOpenHelper {
    Context context;
    public DBhelper(Context context){
        super(context, Constant.DATABASE_NAME,null,Constant.DATABASE_VERSION);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void updateImageTable(final ImageModel imageModel){
       final  SQLiteDatabase db = this.getWritableDatabase();

        try {
            ListGetWebService.getImageBitma(imageModel.getImagepath(),new CallbackDownloadImage(){

                @Override
                public void onSuccess(byte[] result) {
                    ContentValues contentValues=new ContentValues();
                    contentValues.put("Name",imageModel.getImageName());
                    contentValues.put("Image", result);
                    contentValues.put("Type", imageModel.getContent());
                    contentValues.put("ExternalId", Integer.parseInt(imageModel.getId()));
                    db.insert("AllImage", null, contentValues);
                    db.close();
                }

                @Override
                public void onError(String result) {
                    Toast.makeText(context,result,Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
            Toast.makeText(context,exception.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
}
