package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chhota.databaseexample.R;

import java.util.List;

import Model.AllImageModel;
import Model.GridViewImageModle;

/**
 * Created by chhota on 21-02-2016.
 */
public class AdapterContentList extends BaseAdapter{

    List<AllImageModel> imageList;
    Context context;
    private LayoutInflater inflater;
    public AdapterContentList(Context context, List<AllImageModel> list) {
        this.imageList=list;
        this.context=context;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView=inflater.inflate(R.layout.adapter_content_list,parent,false);
            holder=new ViewHolder();
            holder.image=(ImageView)convertView.findViewById(R.id.imageView1);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.image.setImageBitmap(imageList.get(position).getImage());
        //Picasso.with(context).load(Constant.ip+imageModelList.get(position).getImagepath()).into(holder.image);


        return convertView;
    }
    private  class ViewHolder{
        ImageView image;
    }
}
