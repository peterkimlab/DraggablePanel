package com.edxdn.hmsoon.ui.youtube;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.exam.english.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.SpriteFactory;
import com.github.ybq.android.spinkit.Style;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.entities.YoutubeChannel;
import com.edxdn.hmsoon.util.HummingUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EpisodeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = EpisodeAdapter.class.getSimpleName();

    private Context context;
    private List<Datums> playlists;
    private EpisodeFragment.EpisodeListener listener;
    private Integer seletedPosition = null;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    public EpisodeAdapter(Context context, List<Datums> playlists, EpisodeFragment.EpisodeListener listener) {
        this.context = context;
        this.playlists = playlists;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_episode_header, parent, false);
            return new EpisodeHeaderViewHolder(view);
        } else if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_episode, parent, false);
            return new EpisodeViewHolder(view);
        }

        View view = LayoutInflater.from(context).inflate(R.layout.item_episode, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Datums playlistObject = playlists.get(position);
        if (holder instanceof EpisodeHeaderViewHolder) {

            ((EpisodeHeaderViewHolder) holder).refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.refresh();
                }
            });

            YoutubeChannel yc = listener.getChannelInfo();
            if (yc == null) {
                ((EpisodeHeaderViewHolder) holder).channelLayout.setVisibility(View.GONE);
            } else {
                try {
                    ArrayList<HashMap<String, Object>> items = (ArrayList) yc.items;
                    HashMap<String, Object> item = items.get(0);
                    LinkedTreeMap<String, Object> snippet = (LinkedTreeMap) item.get("snippet");
                    LinkedTreeMap<String, Object> thumbnails = (LinkedTreeMap<String, Object>) snippet.get("thumbnails");
                    LinkedTreeMap<String, Object> defaultThumbnails = (LinkedTreeMap<String, Object>) thumbnails.get("medium");
                    LinkedTreeMap<String, Object> statistics = (LinkedTreeMap<String, Object>) item.get("statistics");

                    ((EpisodeHeaderViewHolder) holder).channelLayout.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(defaultThumbnails.get("url").toString()).into(((EpisodeHeaderViewHolder) holder).thumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            ((EpisodeHeaderViewHolder) holder).thumbnail.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            ((EpisodeHeaderViewHolder) holder).thumbnail.setVisibility(View.VISIBLE);
                        }
                    });

                    ((EpisodeHeaderViewHolder) holder).channelDesc.setText(snippet.get("title").toString());
                    if (statistics != null) {
                        ((EpisodeHeaderViewHolder) holder).count.setText("subscriber\n"+statistics.get("subscriberCount").toString());
                    }
                    ((EpisodeHeaderViewHolder) holder).channelLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.pageYoutubeChannel();
                        }
                    });
                } catch (Exception e) {
                    ((EpisodeHeaderViewHolder) holder).channelLayout.setVisibility(View.GONE);
                }
            }
        } else if (holder instanceof EpisodeViewHolder) {
            if (seletedPosition != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (seletedPosition == position) {
                        int color = R.color.selected2;
                        ((EpisodeViewHolder) holder).clickLayout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                        ((EpisodeViewHolder) holder).nowplay.setVisibility(View.VISIBLE);
                        Style style = Style.values()[2];
                        Sprite drawable = SpriteFactory.create(style);
                        ((EpisodeViewHolder) holder).nowplay.setIndeterminateDrawable(drawable);
                    } else {
                        int color = R.color.transparent;
                        ((EpisodeViewHolder) holder).clickLayout.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                        ((EpisodeViewHolder) holder).nowplay.setVisibility(View.GONE);
                    }
                }
            }

            ((EpisodeViewHolder) holder).sentence.setText(playlistObject.source.get(HummingUtils.ElasticField.TEXT_EN).toString());
            ((EpisodeViewHolder) holder).vtitle.setText(playlistObject.source.get(HummingUtils.ElasticField.TITLE).toString());
            String time = "";
            if (playlistObject.source.get(HummingUtils.ElasticField.STIME) != null && playlistObject.source.get(HummingUtils.ElasticField.ETIME) != null) {
                time = HummingUtils.time(playlistObject.source.get(HummingUtils.ElasticField.STIME).toString(), playlistObject.source.get(HummingUtils.ElasticField.ETIME).toString());
            }
            ((EpisodeViewHolder) holder).time.setText(time);
            if (time.equals("")) {
                ((EpisodeViewHolder) holder).time.setVisibility(View.GONE);
            }
            ((EpisodeViewHolder) holder).orderNo.setText(playlistObject.source.get(HummingUtils.ElasticField.NCODE).toString());
            Log.e("test", "===="+(playlistObject.source.get(HummingUtils.ElasticField.NCODE).toString()));
//        Picasso.with(context).load("https://s3.ap-northeast-2.amazonaws.com/my.test0002/"+playlistObject.source.thumbPath).placeholder(R.drawable.ic_menu_gallery).into(holder.thumbnail, new Callback() {
            Picasso.with(context).load(HummingUtils.IMAGE_PATH + playlistObject.source.get(HummingUtils.ElasticField.THUMBNAIL_URL)).into(((EpisodeViewHolder) holder).thumbnail, new Callback() {
                @Override
                public void onSuccess() {
                    ((EpisodeViewHolder) holder).thumbnail.setVisibility(View.VISIBLE);
                }
                @Override
                public void onError() {
                    ((EpisodeViewHolder) holder).thumbnail.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public void setSeletedPosition(int seletedPosition) {
        this.seletedPosition = seletedPosition;
    }

    public int getSeletedPosition() {
        if (seletedPosition == null) {
            return 0;
        }
        return seletedPosition;
    }

    public static  class EpisodeHeaderViewHolder extends RecyclerView.ViewHolder{

        public TextView channelDesc;
        public TextView count;
        public RelativeLayout refresh;
        public LinearLayout layout;
        public LinearLayout channelLayout;
        public CircleImageView thumbnail;

        public EpisodeHeaderViewHolder(View itemView) {
            super(itemView);

            channelDesc = (TextView)itemView.findViewById(R.id.channelDesc);
            count = (TextView)itemView.findViewById(R.id.count);
            refresh = (RelativeLayout)itemView.findViewById(R.id.refresh);
            layout = (LinearLayout)itemView.findViewById(R.id.layout);
            channelLayout = (LinearLayout)itemView.findViewById(R.id.channelLayout);
            thumbnail = (CircleImageView)itemView.findViewById(R.id.thumbnail);
        }
    }

    public class EpisodeViewHolder extends RecyclerView.ViewHolder{

        public TextView sentence;
        public TextView vtitle;
        public TextView time;
        public TextView orderNo;
        public ImageView thumbnail;
        public SpinKitView nowplay;
        public LinearLayout clickLayout;

        public EpisodeViewHolder(View itemView) {
            super(itemView);
            sentence = (TextView)itemView.findViewById(R.id.sentence);
            vtitle = (TextView)itemView.findViewById(R.id.vtitle);
            orderNo = (TextView)itemView.findViewById(R.id.orderNo);
            time = (TextView)itemView.findViewById(R.id.time);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
            nowplay = (SpinKitView)itemView.findViewById(R.id.nowplay);
            clickLayout = (LinearLayout)itemView.findViewById(R.id.clickLayout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}