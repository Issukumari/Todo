package com.app.todo.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.fragment.Fragmentlistviewdetails;
import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    //CallBackItemInterface callback;
    Context context;
    List<TodoHomeDataModel> datamodels;


    public RecyclerAdapter(Context context, List<TodoHomeDataModel> datamodels) {
        this.context = context;
        this.datamodels = datamodels;
        // this.callback = callback;
    }

    public RecyclerAdapter(){

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, final int position) {
        TodoHomeDataModel data = datamodels.get(position);
        holder.et_Title.setText(data.getTitle());
        holder.et_Description.setText(data.getDescription());
        holder.date.setText(datamodels.get(position).getReminderDate());

    }

    @Override
    public int getItemCount() {
        return datamodels.size();
    }

    public void addItem(TodoHomeDataModel Notes) {
        datamodels.add(Notes);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        datamodels.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, datamodels.size());
    }

    public void setList(ArrayList<TodoHomeDataModel> datamodels) {
        this.datamodels.clear();
        notifyDataSetChanged();
        this.datamodels.addAll(datamodels);
        notifyDataSetChanged();
    }

    public TodoHomeDataModel getTodoModel(int pos){
        return datamodels.get(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AppCompatTextView et_Title, et_Description;
        CardView cardview;
        AppCompatTextView date;
        public ViewHolder(final View view) {
            super(view);

            et_Title = (AppCompatTextView) view.findViewById(R.id.textviewcard_title);
            et_Description = (AppCompatTextView) view.findViewById(R.id.card_discriptiomn);
            date=(AppCompatTextView)itemView.findViewById(R.id.reminderdate) ;


            cardview = (CardView) view.findViewById(R.id.card_view);
            cardview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.card_view:

                    Bundle bundle = new Bundle();
                    TodoHomeDataModel dataModel =datamodels.get(getAdapterPosition());
                    Fragmentlistviewdetails fragmentlistview = new Fragmentlistviewdetails(dataModel);
                    bundle.putString("Titletext", dataModel.getTitle());
                    bundle.putString("Desriptiontext", dataModel.getDescription());
                    bundle.putInt("id", dataModel.getId());
                    bundle.putString("startdate",dataModel.getStartdate());
                    fragmentlistview.setArguments(bundle);
                    ((Activity) context).getFragmentManager().beginTransaction().replace(R.id.frame, fragmentlistview).addToBackStack(null).commit();
            }
        }

    }
}