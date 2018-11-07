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
import com.exam.english.R;
import com.test.english.api.Datums;
import com.test.english.application.MyCustomApplication;
import com.test.english.ui.data.ExploreFragmentItemModel;
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
    public int getItemViewType(int position) {

        switch (itemsList.get(position).getvType()) {
            case 0:
                return ExploreFragmentAdapter.SENTENCE_TYPE;
            case 1:
                return ExploreFragmentAdapter.PATTERN_TYPE;
            case 2:
                return ExploreFragmentAdapter.POPULAR_TYPE;
            case 3:
                return ExploreFragmentAdapter.CHAT_TYPE;
            default:
                return -1;
        }
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
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_explore_video_started_type, null);
                return new PopularItemRowHolder(view);
            case ExploreFragmentAdapter.CHAT_TYPE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_explore_chat, null);
                return new ChatItemRowHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {

        ExploreFragmentItemModel singleItem = itemsList.get(i);

        switch (singleItem.getvType()) {
            case ExploreFragmentAdapter.SENTENCE_TYPE:
                if (i % 2 == 0) {
                    ((SentenceItemRowHolder) holder).item_layout.setBackgroundResource(R.drawable.today_pic);
                } else {
                    ((SentenceItemRowHolder) holder).item_layout.setBackgroundResource(R.drawable.today_pic_2);
                }
                ((SentenceItemRowHolder) holder).tvSentence.setText(singleItem.getSentence());
                break;
            case ExploreFragmentAdapter.PATTERN_TYPE:
                if (i % 2 == 0) {
                    ((PatternItemRowHolder) holder).item_layout.setBackgroundResource(R.drawable.today_pic);
                } else {
                    ((PatternItemRowHolder) holder).item_layout.setBackgroundResource(R.drawable.today_pic_2);
                }
                ((PatternItemRowHolder) holder).tvSentence.setText(singleItem.getSentence());
                break;
            case ExploreFragmentAdapter.POPULAR_TYPE:
                Glide.with(mContext)
                        .load(singleItem.getItem_thumbnail())
                        .into(((PopularItemRowHolder) holder).itemImage);
                ((PopularItemRowHolder) holder).tvSentence.setText(singleItem.getSentence());
                break;
            case ExploreFragmentAdapter.CHAT_TYPE:
                ((ChatItemRowHolder) holder).tvSentence.setText(singleItem.getSentence());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public static class SentenceItemRowHolder extends RecyclerView.ViewHolder {
        LinearLayout item_layout;
        TextView tvSentence;
        public SentenceItemRowHolder(View view) {
            super(view);
            this.item_layout = (LinearLayout) view.findViewById(R.id.item_layout);
            this.tvSentence = (TextView) view.findViewById(R.id.sentence);
        }
    }
    public static class PatternItemRowHolder extends RecyclerView.ViewHolder {
        LinearLayout item_layout;
        TextView tvSentence;
        public PatternItemRowHolder(View view) {
            super(view);
            this.item_layout = (LinearLayout) view.findViewById(R.id.item_layout);
            this.tvSentence = (TextView) view.findViewById(R.id.sentence);
        }
    }
    public static class PopularItemRowHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView itemImage;
        TextView tvTime;
        TextView tvSentence;
        public PopularItemRowHolder(View view) {
            super(view);
            this.itemImage = (ImageView) view.findViewById(R.id.thumbnail);
            this.tvTitle = (TextView) view.findViewById(R.id.vtitle);
            this.tvSentence = (TextView) view.findViewById(R.id.sentence);
        }
    }
    public static class ChatItemRowHolder extends RecyclerView.ViewHolder {
        LinearLayout item_layout;
        TextView tvSentence;
        public ChatItemRowHolder(View view) {
            super(view);
            this.item_layout = (LinearLayout) view.findViewById(R.id.item_layout);
            this.tvSentence = (TextView) view.findViewById(R.id.sentence);
        }
    }
}