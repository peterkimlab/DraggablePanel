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

public class ExploreFragmentDataAdapter extends RecyclerView.Adapter<ExploreFragmentDataAdapter.SingleItemRowHolder> {

    private ArrayList<ExploreFragmentItemModel> itemsList;
    private Context mContext;
    List<Datums> mDataList;

    public ExploreFragmentDataAdapter(Context context, ArrayList<ExploreFragmentItemModel> itemsList, List<Datums> dataList) {
        this.mContext = context;
        this.itemsList = itemsList;
        this.mDataList = dataList;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_explore_pattern, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int i) {

        ExploreFragmentItemModel singleItem = itemsList.get(i);

        /*holder.tvTitle.setText(singleItem.getVtitle());
        Glide.with(mContext)
                .load(singleItem.getItem_thumbnail())
                .apply(RequestOptions.centerCropTransform())
                .into(holder.itemImage);
        holder.tvTime.setText(singleItem.getTime());*/

        //holder.item_layout.setBackground();

        if (i % 2 == 0) {
            holder.item_layout.setBackgroundResource(R.drawable.today_pic);
        } else {
            holder.item_layout.setBackgroundResource(R.drawable.today_pic_2);
        }

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

        LinearLayout item_layout;
        TextView tvTitle;
        ImageView itemImage;
        TextView tvTime;
        TextView tvSentence;

        public SingleItemRowHolder(View view) {
            super(view);

            this.item_layout = (LinearLayout) view.findViewById(R.id.item_layout);
            this.tvTime = (TextView) view.findViewById(R.id.time);
            this.itemImage = (ImageView) view.findViewById(R.id.thumbnail);
            this.tvTitle = (TextView) view.findViewById(R.id.vtitle);
            this.tvSentence = (TextView) view.findViewById(R.id.sentence);

        }
    }
}