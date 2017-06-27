package Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.example.chhota.databaseexample.R;

import java.util.ArrayList;

import Model.LocalImageModel;

/**
 * Created by chhota on 10-02-2016.
 */
public class GallaryAdapter extends BaseAdapter {
    Context context;
    ArrayList<LocalImageModel> imageList;
    Bitmap image[];
    private LayoutInflater inflater;
    public  GallaryAdapter(Context context,ArrayList<LocalImageModel> imageList){
        this.context=context;
        this.imageList=imageList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.gallary_adapter, parent, false);
            holder=new ViewHolder();
            holder.image=(ImageView)convertView.findViewById(R.id.imageView);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.image.setImageBitmap(imageList.get(position).getImageBitMap());

        return convertView;
    }
    private  class ViewHolder{
        ImageView image;
    }
}
