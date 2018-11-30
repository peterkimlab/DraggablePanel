package com.edxdn.hmsoon.ui.fragmentcommon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.util.HummingUtils;
import com.edxdn.hmsoon.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ContentsAdapter extends RecyclerView.Adapter<ContentsAdapter.ContentsViewHolder>{

    private static final String TAG = ContentsAdapter.class.getSimpleName();

    private Context context;
    private List<Datums> playlists;

    public ContentsAdapter(Context context, List<Datums> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @Override
    public ContentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular, parent, false);
        return new ContentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContentsViewHolder holder, int position) {
        final Datums playlistObject = playlists.get(position);
        holder.sentence.setText(HummingUtils.getSentenceByMode(playlistObject, context));
        holder.vtitle.setText(HummingUtils.getTitleByMode(playlistObject, context));
        holder.time.setText(HummingUtils.getTime(playlistObject, context));
        if (HummingUtils.isEmpty(holder.time)) {
            holder.time.setVisibility(View.GONE);
        }
        holder.orderNo.setText(position+1+"");

//        Picasso.with(context).load("https://s3.ap-northeast-2.amazonaws.com/my.test0002/"+playlistObject.source.thumbPath).placeholder(R.drawable.ic_menu_gallery).into(holder.thumbnail, new Callback() {
        Picasso.with(context).load(HummingUtils.IMAGE_PATH+playlistObject.source.get(HummingUtils.ElasticField.THUMBNAIL_URL)).into(holder.thumbnail, new Callback() {
            @Override
            public void onSuccess() {
                holder.thumbnail.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                holder.thumbnail.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class ContentsViewHolder extends RecyclerView.ViewHolder{

        public TextView sentence;
        public TextView vtitle;
        public TextView time;
        public TextView orderNo;
        public ImageView thumbnail;

        public ContentsViewHolder(View itemView) {
            super(itemView);

            sentence = (TextView)itemView.findViewById(R.id.sentence);
            vtitle = (TextView)itemView.findViewById(R.id.vtitle);
            time = (TextView)itemView.findViewById(R.id.time);
            orderNo = (TextView)itemView.findViewById(R.id.orderNo);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
        }
    }

}
