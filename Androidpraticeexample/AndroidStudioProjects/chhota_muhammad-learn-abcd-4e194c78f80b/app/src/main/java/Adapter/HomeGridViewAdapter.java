package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.example.chhota.databaseexample.AppController;
import com.example.chhota.databaseexample.R;

import java.util.List;
import java.util.Objects;

import DataBaseConnector.DBhelper;
import Model.GridViewImageModle;
import ProjectConstants.Constant;

/**
 * Created by chhota on 19-02-2016.
 */
public class HomeGridViewAdapter extends BaseAdapter {
    List<GridViewImageModle> imageList;
    Context context;
    private LayoutInflater inflater;
    public HomeGridViewAdapter(Context context, List<GridViewImageModle> list) {
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
            convertView=inflater.inflate(R.layout.home_gridview_adapter,parent,false);
            holder=new ViewHolder();
            holder.image=(ImageView)convertView.findViewById(R.id.imageView1);
            holder.imageName=(TextView)convertView.findViewById(R.id.textView1);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.image.setImageResource(imageList.get(position).getImage());
        holder.imageName.setText(imageList.get(position).getImageName());
        //Picasso.with(context).load(Constant.ip+imageModelList.get(position).getImagepath()).into(holder.image);


        return convertView;
    }
    private  class ViewHolder{
        ImageView image;
        TextView imageName;

    }
}
