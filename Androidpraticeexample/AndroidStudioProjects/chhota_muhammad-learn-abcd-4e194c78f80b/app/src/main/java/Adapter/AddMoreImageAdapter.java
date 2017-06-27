package Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.chhota.databaseexample.AppController;
import com.example.chhota.databaseexample.ImageList;
import com.example.chhota.databaseexample.R;


import java.util.ArrayList;

import DataBaseConnector.DBhelper;
import Model.ImageModel;
import ProjectConstants.Constant;

/**
 * Created by chhota on 11-02-2016.
 */
public class AddMoreImageAdapter extends BaseAdapter {
    Context context;
    ArrayList<ImageModel> imageModelList;
    LayoutInflater inflater;

    public  AddMoreImageAdapter(Context context,ArrayList<ImageModel> imageModelList){
        this.context=context;
        this.imageModelList=imageModelList;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView=inflater.inflate(R.layout.list_adapter,parent,false);
            holder=new ViewHolder();
            holder.image=(NetworkImageView)convertView.findViewById(R.id.imageView);
            holder.imageName=(TextView)convertView.findViewById(R.id.textView);
            holder.add=(Button)convertView.findViewById(R.id.button);
            convertView.setTag(holder);

        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.imageName.setText(imageModelList.get(position).getImageName());
        holder.image.setImageUrl(Constant.ip + imageModelList.get(position).getImagepath(), AppController.getInstance().getImageLoader());

        //Picasso.with(context).load(Constant.ip+imageModelList.get(position).getImagepath()).into(holder.image);

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_menu_add)
                        .setTitle("Add new word")
                        .setMessage("Do you want to add "+imageModelList.get(position).getImageName()+" to your list?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBhelper dBhelper=new DBhelper(context);
                                dBhelper.updateImageTable(imageModelList.get(position));
                                ImageList.modellist.remove(position);
                                notifyDataSetChanged();
                            }

                        }).setNegativeButton("No",null)
                .show();

            }
        });
        return convertView;
    }
    private  class ViewHolder{
        NetworkImageView image;
        TextView imageName;
        Button add;
    }
}
