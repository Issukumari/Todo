package com.example.bridgeit.card;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    ArrayList arrayList=new ArrayList();

    public RecyclerAdapter(Context context ,ArrayList arrlist) {
        this.context=context;
        this.arrayList=arrlist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
       // return datamodels.size();
        return arrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AppCompatTextView et_Title, et_Description;
        CardView cardview;
        AppCompatTextView date;
        AppCompatTextView time;
        LinearLayout linearlayout;

        public ViewHolder(final View view) {
            super(view);

            et_Title = (AppCompatTextView) view.findViewById(R.id.textviewcard_title);
            et_Description = (AppCompatTextView) view.findViewById(R.id.card_discriptiomn);
            date = (AppCompatTextView) itemView.findViewById(R.id.reminderdate);
            time=(AppCompatTextView)itemView.findViewById(R.id.remindertimes);
            linearlayout = (LinearLayout) view.findViewById(R.id.linearlayoutcard);
            cardview = (CardView) view.findViewById(R.id.card_view);
            cardview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.card_view:

            }

        }
    }
}