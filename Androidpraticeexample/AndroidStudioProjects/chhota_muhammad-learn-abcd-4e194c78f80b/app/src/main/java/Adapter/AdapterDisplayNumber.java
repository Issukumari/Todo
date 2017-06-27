package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chhota.databaseexample.R;

import java.util.Random;

import Utilities.NumberToWord;

/**
 * Created by chhota on 03-03-2016.
 */
public class AdapterDisplayNumber extends BaseAdapter {
    Context context;
    private LayoutInflater inflater;
    int number[];
    int n=10;
    String color[]=new String[]{"#3F51B5","#FF0000","#00FF00","#0000FF","#3F51FF","#00ddFF","#FFdd00","#00ddFF","#d00dFF","#3FF945","#5F9366","#3F51B5"};
    NumberToWord numberToWord;

    public  AdapterDisplayNumber(Context context,int number[]){
        this.context=context;
        this.inflater = LayoutInflater.from(context);
        this.number=number;
        numberToWord=new NumberToWord();
    }

    @Override
    public int getCount() {
        return number.length;
    }

    @Override
    public Object getItem(int position) {
        return number[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.adapter_display_number, parent, false);
            holder=new ViewHolder();
            holder.number=(TextView)convertView.findViewById(R.id.number);
            holder.numberWord=(TextView)convertView.findViewById(R.id.numberWord);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.number.setText(""+number[position]);
        holder.numberWord.setText(numberToWord.convertNumberToWords(number[position]));
        holder.number.setTextColor(Color.parseColor(color[new Random().nextInt(n)]));
        holder.numberWord.setTextColor(Color.parseColor(color[new Random().nextInt(n)]));
        return convertView;
    }


    private  class ViewHolder{
        TextView number;
        TextView numberWord;
    }
}
