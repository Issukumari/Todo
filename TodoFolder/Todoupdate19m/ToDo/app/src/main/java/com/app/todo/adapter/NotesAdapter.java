package com.app.todo.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.fragment.NotesFragment;
import com.app.todo.model.DataModel;

import java.util.List;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    Context context;
    List<DataModel> datamodels;

    public NotesAdapter(NotesFragment notesFragment, List<DataModel> datamodels) {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotesAdapter.ViewHolder holder, int position) {
        DataModel data = datamodels.get(position);

        holder.et_Title.setText(data.getTitle());
        holder.et_Description.setText(data.getDescription());
    }


    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView et_Title, et_Description;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            et_Title = (AppCompatTextView) itemView.findViewById(R.id.textviewcard_title);
            et_Description = (AppCompatTextView) itemView.findViewById(R.id.card_discriptiomn);
            cardview = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}
