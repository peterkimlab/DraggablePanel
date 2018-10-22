package com.test.english.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.exam.english.R;
import com.test.english.api.Datums;
import com.test.english.ui.data.MusicFragmentItemModel;
import com.test.english.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class MusicFragmentDataAdapter extends RecyclerView.Adapter<MusicFragmentDataAdapter.SingleItemRowHolder> {

    private ArrayList<MusicFragmentItemModel> itemsList;
    private Context mContext;
    List<Datums> mDataList;

    public MusicFragmentDataAdapter(Context context, ArrayList<MusicFragmentItemModel> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    public MusicFragmentDataAdapter(Context context, ArrayList<MusicFragmentItemModel> itemsList, List<Datums> dataList) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_music_image_type, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int i) {

        MusicFragmentItemModel singleItem = itemsList.get(i);

        holder.tvTitle.setText(singleItem.getVtitle());
        Glide.with(mContext)
                .load(singleItem.getItem_thumbnail())
                .apply(RequestOptions.centerCropTransform())
                .into(holder.itemImage);
        holder.tvTime.setText(singleItem.getTime());
        holder.tvSentence.setText(singleItem.getSentence());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).setVideoUrl(mDataList.get(i));
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public static class SingleItemRowHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView itemImage;
        TextView tvTime;
        TextView tvSentence;

        public SingleItemRowHolder(View view) {
            super(view);

            this.tvTime = (TextView) view.findViewById(R.id.time);
            this.itemImage = (ImageView) view.findViewById(R.id.thumbnail);
            this.tvTitle = (TextView) view.findViewById(R.id.vtitle);
            this.tvSentence = (TextView) view.findViewById(R.id.sentence);

            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/
        }
    }
}