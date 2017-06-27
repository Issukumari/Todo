package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.chhota.databaseexample.R;
import java.util.ArrayList;
import Model.LocalImageModel;
/**
 * Created by chhota on 10-03-2016.
 */
public class AdapterRemaningDisplay extends BaseAdapter{
    Context context;
    private LayoutInflater inflater;
    ArrayList<LocalImageModel> imageList;

    public  AdapterRemaningDisplay(Context context,ArrayList<LocalImageModel> imageList){
        this.context=context;
        this.inflater = LayoutInflater.from(context);
        this.imageList=imageList;
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
            convertView = inflater.inflate(R.layout.adapter_remaning_display, parent, false);
            holder=new ViewHolder();
            holder.image=(ImageView)convertView.findViewById(R.id.image);
            holder.imageName=(TextView)convertView.findViewById(R.id.imageName);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.image.setImageBitmap(imageList.get(position).getImageBitMap());
        holder.imageName.setText(imageList.get(position).getImageName());
        return convertView;
    }


    private  class ViewHolder{
        ImageView image;
        TextView imageName;
    }
}
