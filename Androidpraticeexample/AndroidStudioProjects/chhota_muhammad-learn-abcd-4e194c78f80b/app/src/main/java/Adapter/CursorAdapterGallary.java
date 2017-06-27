package Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.example.chhota.databaseexample.R;

/**
 * Created by chhota on 15-02-2016.
 */
public class CursorAdapterGallary extends SimpleCursorAdapter {
    private LayoutInflater inflater;
    Context context;
    Cursor cursor;
    int layout;


    public CursorAdapterGallary(Context context, int layout, Cursor coursour, String[] from, int[] to, int flags){
        super(context, layout, coursour, from, to,flags);
        this.inflater=LayoutInflater.from(context);
        this.context=context;
        this.cursor=cursor;
        this.layout=layout;
    }

    @Override
    public View newView(Context ctx, Cursor cursor, ViewGroup parent) {
        View vView = inflater.inflate(layout, parent, false);
        ViewHolder holder=new ViewHolder();
        holder.image=(ImageView)vView.findViewById(R.id.imageView);
        vView.setTag(holder);
        // no need to bind data here. you do in later
        return vView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder  =   (ViewHolder)    view.getTag();
        holder.image.setImageBitmap(getImageBitmap(cursor.getBlob(2)));
    }

    public Bitmap getImageBitmap(byte imagebyte[]){
        return BitmapFactory.decodeByteArray(imagebyte, 0, imagebyte.length);
    }

    private  class ViewHolder{
        ImageView image;
    }


}
