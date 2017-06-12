package com.app.todo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.todo.R;
import com.app.todo.archievefragment.ui.ArchieveFragment;
import com.app.todo.fragment.Fragmentlistviewdetails;
import com.app.todo.notes.ui.NotesFragment;
import com.app.todo.reminderfragment.ui.ReminderFragment;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.updateactivity.NoteUpdateActivity;

import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<TodoHomeDataModel> datamodels;
    private OnLongClickListener onLongClickListener;
    Fragmentlistviewdetails fragmentlistview;
    private Fragment  archieveFragment;
    NotesFragment notesFragment;
    ReminderFragment reminderFragment;
    public RecyclerAdapter(Context context, List<TodoHomeDataModel> datamodels) {
        this.context = context;
        this.datamodels = datamodels;
    }

    public RecyclerAdapter(Context context) {
        this.context = context;
        datamodels = new ArrayList<>();
    }

    public RecyclerAdapter(Activity activity, List<TodoHomeDataModel> datamodels, NotesFragment notesFragment) {
        this.context = activity;
        this.datamodels = datamodels;
        this.notesFragment=notesFragment;

    }
    public RecyclerAdapter(Activity activity, List<TodoHomeDataModel> datamodels, ReminderFragment reminderFragment) {
        this.context = activity;
        this.datamodels = datamodels;
        this.reminderFragment = reminderFragment;
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
        holder.date.setText(data.getReminderDate());
        if (data.getColor() != null) {
            holder.linearlayout.setBackgroundColor(Integer.parseInt(data.getColor()));
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onLongClickListener.onLongClick((TodoHomeDataModel) datamodels);
                return true;
            }
        });
        holder.cardview.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                return false;
            }
        });
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

    public void setList(List<TodoHomeDataModel> datamodels) {
        this.datamodels.clear();
        notifyDataSetChanged();
        this.datamodels.addAll(datamodels);
        notifyDataSetChanged();

    }

    public void setFilter(List<TodoHomeDataModel> li) {
        datamodels = new ArrayList<>();
        datamodels.addAll(li);
        notifyDataSetChanged();
    }

    public TodoHomeDataModel getTodoModel(int pos) {
        return datamodels.get(pos);
    }

    public void archieveitem(int position) {

        notifyDataSetChanged();
        notifyItemRangeChanged(position, datamodels.size());
    }

    public void deleteditem(int position) {
        datamodels.remove(position);
        notifyDataSetChanged();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, datamodels.size());
    }

    public void setColor(int color) {
        fragmentlistview.setColor(color);
    }


    public interface OnLongClickListener {
        void onLongClick(TodoHomeDataModel datmodels);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AppCompatTextView et_Title, et_Description;
        CardView cardview;
        AppCompatTextView date;
        LinearLayout linearlayout;

        public ViewHolder(final View view) {
            super(view);

            et_Title = (AppCompatTextView) view.findViewById(R.id.textviewcard_title);
            et_Description = (AppCompatTextView) view.findViewById(R.id.card_discriptiomn);
            date = (AppCompatTextView) itemView.findViewById(R.id.reminderdate);
            linearlayout = (LinearLayout) view.findViewById(R.id.linearlayoutcard);
            cardview = (CardView) view.findViewById(R.id.card_view);
            cardview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.card_view:
                    /*Bundle bundle = new Bundle();
                    TodoHomeDataModel dataModel = datamodels.get(getAdapterPosition());
                    fragmentlistview = new Fragmentlistviewdetails(dataModel);
                    bundle.putString("Titletext", dataModel.getTitle());
                    bundle.putString("Desriptiontext", dataModel.getDescription());
                    bundle.putInt("id", dataModel.getId());
                    bundle.putString("startdate", dataModel.getStartdate());
                    bundle.putString("color",dataModel.getColor());
                    fragmentlistview.setArguments(bundle);
                    ((Activity) context).getFragmentManager().beginTransaction()
                            .addSharedElement(cardview, ViewCompat.getTransitionName(cardview))                            .replace(R.id.frame, fragmentlistview)
                            .addToBackStack(null)
                            .commit();

*/

                  if (notesFragment!=null||reminderFragment!=null) {
                        Intent intent = new Intent(context, NoteUpdateActivity.class);
                        Bundle bundle = new Bundle();
                        TodoHomeDataModel dataModel = datamodels.get(getAdapterPosition());
                        bundle.putString("Titletext", dataModel.getTitle());
                        bundle.putString("Desriptiontext", dataModel.getDescription());
                        bundle.putInt("id", dataModel.getId());
                        bundle.putString("startdate", dataModel.getStartdate());
                        bundle.putString("color", dataModel.getColor());
                        intent.putExtras(bundle);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                                        cardview, context.getString(R.string.custom_transition));
                        ActivityCompat.startActivity((Activity)context,intent, options.toBundle());
                    } else {

                    }
                    break;
            }

        }
    }
}