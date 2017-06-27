package com.app.todo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.Fragment.Fragmentlistviewdetails;
import com.app.todo.R;
import com.app.todo.ui.NoteActivity;

import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<String> data;

    public RecyclerAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.card_textView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView card_textView;
        public ViewHolder(final View itemView) {
            super(itemView);
            card_textView = (AppCompatTextView) itemView.findViewById(R.id.card_textView);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                   /* Intent intent = new Intent(itemView.getContext(), NoteActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("text", card_textView.getText().toString());
                    intent.putExtra("name", bundle);
                    itemView.getContext().startActivity(intent);
*/

                    Fragmentlistviewdetails f1 = new Fragmentlistviewdetails();
                    Bundle bundle = new Bundle();
                    bundle.putString("text", card_textView.getText().toString());
                    f1.setArguments(bundle);
                    ((Activity) context).getFragmentManager().beginTransaction().replace(R.id.frame,f1).addToBackStack(null).commit();
                }
            });
        }
    }
}


