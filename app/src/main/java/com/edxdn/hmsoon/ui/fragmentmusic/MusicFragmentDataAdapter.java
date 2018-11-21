package com.edxdn.hmsoon.ui.fragmentmusic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.edxdn.hmsoon.ui.data.ExploreFragmentItemModel;
import com.exam.english.R;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.ui.main.MainActivity;
import java.util.ArrayList;
import java.util.List;

public class MusicFragmentDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ExploreFragmentItemModel> itemsList;
    private Context mContext;
    List<Datums> mDataList;

    public MusicFragmentDataAdapter(Context context, ArrayList<ExploreFragmentItemModel> itemsList, List<Datums> dataList) {
        this.mContext = context;
        this.itemsList = itemsList;
        this.mDataList = dataList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (itemsList.get(position).getvType()) {
            case 0:
                return MusicFragmentAdapter.RANKING_TYPE;
            case 1:
                return MusicFragmentAdapter.RECENT_TYPE;
            case 2:
                return MusicFragmentAdapter.MOTHER_GOOSE_TYPE;
            case 3:
                return MusicFragmentAdapter.RECOMMEND_TYPE;
            default:
                return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        switch (i) {
            case MusicFragmentAdapter.RANKING_TYPE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_music_video_ranking_type, null);
                return new MusicFragmentDataAdapter.RankingItemRowHolder(view);
            case MusicFragmentAdapter.RECENT_TYPE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_music_video_recent_type, null);
                return new RecentItemRowHolder(view);
            case MusicFragmentAdapter.MOTHER_GOOSE_TYPE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_explore_video_started_type, null);
                return new MusicFragmentDataAdapter.PopularItemRowHolder(view);
            case MusicFragmentAdapter.RECOMMEND_TYPE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_music_video_recommend_type, null);
                return new RecommendItemRowHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int i) {

        final ExploreFragmentItemModel singleItem = itemsList.get(i);

        switch (singleItem.getvType()) {
            case MusicFragmentAdapter.RANKING_TYPE:
                Glide.with(mContext)
                        .load(singleItem.getItem_thumbnail())
                        .into(((MusicFragmentDataAdapter.RankingItemRowHolder) holder).itemImage);

                ((MusicFragmentDataAdapter.RankingItemRowHolder) holder).tvSentence.setText(singleItem.getSentence());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity)mContext).setMusicVideoUrl(mDataList.get(i));
                    }
                });
                break;
            case MusicFragmentAdapter.RECENT_TYPE:
                Glide.with(mContext)
                        .load(singleItem.getItem_thumbnail())
                        .into(((MusicFragmentDataAdapter.RecentItemRowHolder) holder).itemImage);

                ((MusicFragmentDataAdapter.RecentItemRowHolder) holder).tvSentence.setText(singleItem.getSentence());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity)mContext).setMusicVideoUrl(mDataList.get(i));
                    }
                });
                break;
            case MusicFragmentAdapter.MOTHER_GOOSE_TYPE:
                Glide.with(mContext)
                        .load(singleItem.getItem_thumbnail())
                        .into(((MusicFragmentDataAdapter.PopularItemRowHolder) holder).itemImage);

                ((MusicFragmentDataAdapter.PopularItemRowHolder) holder).tvSentence.setText(singleItem.getSentence());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity)mContext).setMusicVideoUrl(mDataList.get(i));
                    }
                });
                break;
            case MusicFragmentAdapter.RECOMMEND_TYPE:
                Glide.with(mContext)
                        .load(singleItem.getItem_thumbnail())
                        .into(((RecommendItemRowHolder) holder).itemImage);

                ((RecommendItemRowHolder) holder).tvSentence.setText(singleItem.getSentence());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity)mContext).setMusicVideoUrl(mDataList.get(i));
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {

        if (itemsList.get(0).getvType() == MusicFragmentAdapter.RANKING_TYPE) {
            return 6;
        } else if (itemsList.get(0).getvType() == MusicFragmentAdapter.MOTHER_GOOSE_TYPE){
            return 4;
        }

        return (null != itemsList ? itemsList.size() : 0);
    }

    public static class RankingItemRowHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView itemImage;
        TextView tvTime;
        TextView tvSentence;
        public RankingItemRowHolder(View view) {
            super(view);
            this.itemImage = (ImageView) view.findViewById(R.id.thumbnail);
            this.tvTitle = (TextView) view.findViewById(R.id.vtitle);
            this.tvSentence = (TextView) view.findViewById(R.id.sentence);
        }
    }
    public static class RecentItemRowHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView itemImage;
        TextView tvTime;
        TextView tvSentence;
        public RecentItemRowHolder(View view) {
            super(view);
            this.itemImage = (ImageView) view.findViewById(R.id.thumbnail);
            this.tvTitle = (TextView) view.findViewById(R.id.vtitle);
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
    public static class RecommendItemRowHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView itemImage;
        TextView tvTime;
        TextView tvSentence;
        public RecommendItemRowHolder(View view) {
            super(view);
            this.itemImage = (ImageView) view.findViewById(R.id.thumbnail);
            this.tvTitle = (TextView) view.findViewById(R.id.vtitle);
            this.tvSentence = (TextView) view.findViewById(R.id.sentence);
        }
    }
}