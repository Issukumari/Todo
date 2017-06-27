package com.app.todo.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.model.NotesModel;
import com.app.todo.ui.CallBackItemClick;

import java.util.List;

/**
 * Created by bridgeit on 3/4/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TaskViewHolder> {
    Context context;
    List<NotesModel> model;

    CallBackItemClick clickListener;
    public RecyclerAdapter(Context context, List<NotesModel> model, CallBackItemClick listener) {
        this.model = model;
        this.context = context;
        clickListener = listener;

    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.activity_todonotes, parent, false);
        TaskViewHolder myViewHolder = new TaskViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.TaskViewHolder holder, final int position) {
        holder.titleTextView.setText(model.get(position).getTitle());
        holder.dateTextview.setText(model.get(position).getDate());
        holder.contentTextview.setText(model.get(position).getContent());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(model.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {

        return model.size();
    }

    public void addNotes(NotesModel note) {

        model.add(note);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        model.remove(position);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, model.size());
    }
    public void archiveItem(int position){

        notifyDataSetChanged();
        notifyItemRangeChanged(position,model.size());
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView titleTextView, dateTextview, contentTextview;
        CardView cardView;
        public TaskViewHolder(final View itemView) {
            super(itemView);
            titleTextView = (AppCompatTextView) itemView.findViewById(R.id.title_TextView);
            dateTextview = (AppCompatTextView) itemView.findViewById(R.id.date_TextView);
            contentTextview = (AppCompatTextView) itemView.findViewById(R.id.content_TextView);
            cardView = (CardView) itemView.findViewById(R.id.myCardView);

        }
    }

}
