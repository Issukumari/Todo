package bridgelabz.app.com.listviewswipetest.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import bridgelabz.app.com.listviewswipetest.Fragment.Fragmentlistviewdetails;
import bridgelabz.app.com.listviewswipetest.R;
import bridgelabz.app.com.listviewswipetest.model.DataModel;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<DataModel> datamodels;
    public RecyclerAdapter(Context context, List<DataModel> data) {
        this.context = context;
        this.datamodels = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataModel data=datamodels.get(position);

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
    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView  et_Title,et_Description;

        public ViewHolder(final View view) {
            super(view);

            et_Title= (AppCompatTextView) view.findViewById(R.id.textviewcard_title);
            et_Description=(AppCompatTextView) view.findViewById(R.id.card_discriptiomn);
           // card_textView2 = (AppCompatTextView) view.findViewById(R.id.card_textView2);
            view.setOnClickListener(new View.OnClickListener() {
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

                    bundle.putString("text", et_Title.getText().toString());
                    bundle.putString("text1", et_Description.getText().toString());

                    //bundle.putString("text2", card_textView.getText().toString());
                    f1.setArguments(bundle);
                    ((Activity) context).getFragmentManager().beginTransaction().replace(R.id.frame, f1).addToBackStack(null).commit();
                }
            });
        }
    }
}


