package com.app.todo.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.model.DataModel;
import com.app.todo.ui.CallBackItemInterface;

import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements View.OnClickListener {
    CallBackItemInterface callback;
    Context context;
    List<DataModel> datamodels;

    public RecyclerAdapter(Context context, List<DataModel> data, CallBackItemInterface callback) {
        this.context = context;
        this.datamodels = data;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        DataModel data = datamodels.get(position);

        holder.et_Title.setText(data.getTitle());
        holder.et_Description.setText(data.getDescription());
    }


    @Override
    public int getItemCount() {
        return datamodels.size();
    }

    public void addItem(DataModel Notes) {
        datamodels.add(Notes);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        datamodels.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, datamodels.size());
    }

    @Override
    public void onClick(View view) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AppCompatTextView et_Title, et_Description;
        CardView cardview;

        public ViewHolder(final View view) {
            super(view);

            et_Title = (AppCompatTextView) view.findViewById(R.id.textviewcard_title);
            et_Description = (AppCompatTextView) view.findViewById(R.id.card_discriptiomn);
            cardview = (CardView) view.findViewById(R.id.card_view);
            cardview.setOnClickListener(this);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.card_view:
                    callback.onItemClick(datamodels.get(getAdapterPosition()));
                    datamodels.remove(getAdapterPosition());
                    break;
            }
        }
    }
}


