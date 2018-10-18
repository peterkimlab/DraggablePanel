package com.test.english.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.exam.english.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.test.english.api.Datums;
import com.test.english.util.HummingUtils;

import java.util.List;

public class MusicFragementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = MusicFragementAdapter.class.getSimpleName();

    private Context context;
    private List<Datums> playlists;

    public MusicFragementAdapter(Context context, List<Datums> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music_image_type, parent, false);
        return new ImageTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Datums playlistObject = playlists.get(position);
        //holder.sentence.setText(HummingUtils.getSentenceByMode(playlistObject, context));
        //holder.vtitle.setText(HummingUtils.getTitleByMode(playlistObject, context));
        ((ImageTypeViewHolder) holder).time.setText(HummingUtils.getTime(playlistObject, context));

        if (HummingUtils.isEmpty(((ImageTypeViewHolder) holder).time)) {
            ((ImageTypeViewHolder) holder).time.setVisibility(View.GONE);
        }

        Picasso.with(context).load(HummingUtils.IMAGE_PATH + playlistObject.source.get(HummingUtils.ElasticField.THUMBNAIL_URL)).into(((ImageTypeViewHolder) holder).thumbnail, new Callback() {
            @Override
            public void onSuccess() {
                ((ImageTypeViewHolder) holder).thumbnail.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                ((ImageTypeViewHolder) holder).thumbnail.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {

        private TextView sentence;
        private TextView vtitle;
        private TextView time;
        private ImageView thumbnail;

        public ImageTypeViewHolder(View itemView) {
            super(itemView);
            sentence = (TextView)itemView.findViewById(R.id.sentence);
            vtitle = (TextView)itemView.findViewById(R.id.vtitle);
            time = (TextView)itemView.findViewById(R.id.time);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
        }
    }
}
