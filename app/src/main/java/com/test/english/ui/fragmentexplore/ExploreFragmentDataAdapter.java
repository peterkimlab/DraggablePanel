package com.test.english.ui.fragmentexplore;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.exam.english.R;
import com.test.english.api.Datums;
import com.test.english.ui.data.ExploreFragmentItemModel;
import com.test.english.ui.main.MainActivity;
import java.util.ArrayList;
import java.util.List;

public class ExploreFragmentDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ExploreFragmentItemModel> itemsList;
    private Context mContext;
    List<Datums> mDataList;

    public ExploreFragmentDataAdapter(Context context, ArrayList<ExploreFragmentItemModel> itemsList, List<Datums> dataList) {
        this.mContext = context;
        this.itemsList = itemsList;
        this.mDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        switch (i) {
            case ExploreFragmentAdapter.SENTENCE_TYPE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_explore_pattern, null);
                return new SentenceItemRowHolder(view);
            case ExploreFragmentAdapter.PATTERN_TYPE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_explore_pattern, null);
                return new PatternItemRowHolder(view);
            case ExploreFragmentAdapter.POPULAR_TYPE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_explore_video_startd_type, null);
                return new PatternItemRowHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {

        ExploreFragmentItemModel singleItem = itemsList.get(i);

        if (holder instanceof SentenceItemRowHolder) {
            if (i % 2 == 0) {
                ((SentenceItemRowHolder) holder).item_layout.setBackgroundResource(R.drawable.today_pic);
            } else {
                ((SentenceItemRowHolder) holder).item_layout.setBackgroundResource(R.drawable.today_pic_2);
            }
            ((SentenceItemRowHolder) holder).tvSentence.setText(singleItem.getSentence());
            ((SentenceItemRowHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)mContext).setVideoUrl(mDataList.get(i));
                }
            });
        } else if (holder instanceof PatternItemRowHolder) {
            if (i % 2 == 0) {
                ((SentenceItemRowHolder) holder).item_layout.setBackgroundResource(R.drawable.today_pic);
            } else {
                ((SentenceItemRowHolder) holder).item_layout.setBackgroundResource(R.drawable.today_pic_2);
            }
            ((SentenceItemRowHolder) holder).tvSentence.setText(singleItem.getSentence());
            ((SentenceItemRowHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)mContext).setVideoUrl(mDataList.get(i));
                }
            });
        } else if (holder instanceof PopularItemRowHolder) {
            Glide.with(mContext)
                    .load(singleItem.getItem_thumbnail())
                    .apply(RequestOptions.centerCropTransform())
                    .into(((PopularItemRowHolder) holder).itemImage);
            ((PopularItemRowHolder) holder).tvTime.setText(singleItem.getTime());
            ((PopularItemRowHolder) holder).tvSentence.setText(singleItem.getSentence());
        }

        /*switch (i) {
            case ExploreFragmentAdapter.SENTENCE_TYPE:

                break;
            case ExploreFragmentAdapter.PATTERN_TYPE:

                break;
            case ExploreFragmentAdapter.POPULAR_TYPE:

                break;
        }*/
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public static class SentenceItemRowHolder extends RecyclerView.ViewHolder {

        LinearLayout item_layout;
        TextView tvTitle;
        ImageView itemImage;
        TextView tvTime;
        TextView tvSentence;

        public SentenceItemRowHolder(View view) {
            super(view);

            this.item_layout = (LinearLayout) view.findViewById(R.id.item_layout);
            this.tvTime = (TextView) view.findViewById(R.id.time);
            this.itemImage = (ImageView) view.findViewById(R.id.thumbnail);
            this.tvTitle = (TextView) view.findViewById(R.id.vtitle);
            this.tvSentence = (TextView) view.findViewById(R.id.sentence);
        }
    }

    public static class PatternItemRowHolder extends RecyclerView.ViewHolder {

        LinearLayout item_layout;
        TextView tvTitle;
        ImageView itemImage;
        TextView tvTime;
        TextView tvSentence;

        public PatternItemRowHolder(View view) {
            super(view);

            this.item_layout = (LinearLayout) view.findViewById(R.id.item_layout);
            this.tvTime = (TextView) view.findViewById(R.id.time);
            this.itemImage = (ImageView) view.findViewById(R.id.thumbnail);
            this.tvTitle = (TextView) view.findViewById(R.id.vtitle);
            this.tvSentence = (TextView) view.findViewById(R.id.sentence);
        }
    }

    public static class PopularItemRowHolder extends RecyclerView.ViewHolder {

        LinearLayout item_layout;
        TextView tvTitle;
        ImageView itemImage;
        TextView tvTime;
        TextView tvSentence;

        public PopularItemRowHolder(View view) {
            super(view);

            this.item_layout = (LinearLayout) view.findViewById(R.id.item_layout);
            this.tvTime = (TextView) view.findViewById(R.id.time);
            this.itemImage = (ImageView) view.findViewById(R.id.thumbnail);
            this.tvTitle = (TextView) view.findViewById(R.id.vtitle);
            this.tvSentence = (TextView) view.findViewById(R.id.sentence);
        }
    }
}