package com.test.english.ui.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.exam.english.R;

public class MainItemDisplay03ViewHolder extends RecyclerView.ViewHolder{

    public TextView sentence;
    public TextView vtitle;
    public TextView time;
    public ImageView thumbnail;

    public MainItemDisplay03ViewHolder(View itemView) {
        super(itemView);
        sentence = (TextView)itemView.findViewById(R.id.sentence);
        vtitle = (TextView)itemView.findViewById(R.id.vtitle);
        time = (TextView)itemView.findViewById(R.id.time);
        thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
    }
}
