package com.edxdn.hmsoon.ui.youtube;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.util.HummingUtils;
import com.exam.english.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlayMusicAdapter extends RecyclerView.Adapter<PlayMusicAdapter.PlayMusicHolder>{

    private static final String TAG = PlayMusicAdapter.class.getSimpleName();

    private Context context;
    private List<Datums> playlists;
    private Integer seletedPosition = null;

    public PlayMusicAdapter(Context context, List<Datums> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @Override
    public PlayMusicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playmusic, parent, false);
        return new PlayMusicHolder(view);
    }

    public void setSeletedPosition(int seletedPosition){
        this.seletedPosition = seletedPosition;
    }

    public int getSeletedPosition() {
        if(seletedPosition == null) {
            return 0;
        }
        return seletedPosition;
    }

    @Override
    public void onBindViewHolder(final PlayMusicHolder holder, int position) {
        final Datums playlistObject = playlists.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (seletedPosition != null && seletedPosition == position) {
                int color = R.color.selected2;
                holder.clickLayout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
//                holder.nowplay.setVisibility(View.VISIBLE);
//                Style style = Style.values()[2];
//                Sprite drawable = SpriteFactory.create(style);
//                holder.nowplay.setIndeterminateDrawable(drawable);

            } else {
                int color = R.color.transparent;
                holder.clickLayout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
//                holder.nowplay.setVisibility(View.GONE);
            }
        }

        holder.sentence.setText(HummingUtils.getSentenceByMode(playlistObject, context));
        //holder.vtitle.setText(HummingUtils.getTitleByMode(playlistObject, context));
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

    public class PlayMusicHolder extends RecyclerView.ViewHolder{

        public TextView sentence;
        //public TextView vtitle;
        public TextView time;
        public TextView orderNo;
        public ImageView thumbnail;
        SpinKitView nowplay;
        public LinearLayout clickLayout;

        public PlayMusicHolder(View itemView) {
            super(itemView);

            sentence = (TextView)itemView.findViewById(R.id.sentence);
            //vtitle = (TextView)itemView.findViewById(R.id.vtitle);
            time = (TextView)itemView.findViewById(R.id.time);
            orderNo = (TextView)itemView.findViewById(R.id.orderNo);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
            nowplay = (SpinKitView)itemView.findViewById(R.id.nowplay);
            clickLayout = (LinearLayout)itemView.findViewById(R.id.clickLayout);
        }
    }
}
