package com.test.english.ui.youtube;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.exam.english.R;


public class RelationViewHolder extends RecyclerView.ViewHolder{

    public TextView sentence;
    public TextView vtitle;
    public TextView time;
    public LinearLayout clickLayout;
    public ImageView thumbnail;

    public RelationViewHolder(View itemView) {
        super(itemView);

        sentence = (TextView)itemView.findViewById(R.id.sentence);
        vtitle = (TextView)itemView.findViewById(R.id.vtitle);
        time = (TextView)itemView.findViewById(R.id.time);
        clickLayout = (LinearLayout)itemView.findViewById(R.id.clickLayout);
        thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
    }
}
