package bridgelabz.app.com.listtodo;

/**
 * Created by bridgeit on 28/3/17.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
 Context context;

   public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewVersion;
        ImageView imageViewIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewVersion = (TextView) itemView.findViewById(R.id.textViewDescription);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imagelisttodo);
        }
    }

   // public CustomAdapter(ArrayList<DataModel> data) {
      //  this.dataSet = data;
    //}

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);

        view.setOnClickListener(MainActivity.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

       /* DataModel dataModel = dataSet.get(listPosition);

        holder.textViewName.setText(dataModel.getName());
        holder.textViewVersion.setText(dataModel.getVersion());
        holder.imageViewIcon.setImageResource(dataModel.getImage());*/
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
