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

public class MainItemDisplay03Adapter extends RecyclerView.Adapter<MainItemDisplay03Adapter.MainItemDisplay03ViewHolder> {

    private static final String TAG = MainItemDisplay03Adapter.class.getSimpleName();

    private Context context;
    private List<Datums> playlists;

    public MainItemDisplay03Adapter(Context context, List<Datums> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @Override
    public MainItemDisplay03ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discover_display03, parent, false);
        return new MainItemDisplay03ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainItemDisplay03ViewHolder holder, int position) {
        final Datums playlistObject = playlists.get(position);
        //holder.sentence.setText(HummingUtils.getSentenceByMode(playlistObject, context));
        //holder.vtitle.setText(HummingUtils.getTitleByMode(playlistObject, context));
        holder.time.setText(HummingUtils.getTime(playlistObject, context));
        if (HummingUtils.isEmpty(holder.time)) {
            holder.time.setVisibility(View.GONE);
        }

        Picasso.with(context).load(HummingUtils.IMAGE_PATH + playlistObject.source.get(HummingUtils.ElasticField.THUMBNAIL_URL)).into(holder.thumbnail, new Callback() {
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

    public static class MainItemDisplay03ViewHolder extends RecyclerView.ViewHolder {

        private TextView sentence;
        private TextView vtitle;
        private TextView time;
        private ImageView thumbnail;

        public MainItemDisplay03ViewHolder(View itemView) {
            super(itemView);
            sentence = (TextView)itemView.findViewById(R.id.sentence);
            vtitle = (TextView)itemView.findViewById(R.id.vtitle);
            time = (TextView)itemView.findViewById(R.id.time);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
        }
    }
}
