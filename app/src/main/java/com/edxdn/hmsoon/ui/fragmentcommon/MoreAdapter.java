package com.edxdn.hmsoon.ui.fragmentcommon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.edxdn.hmsoon.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.util.HummingUtils;
import java.util.List;

public class MoreAdapter extends RecyclerView.Adapter<MoreAdapter.MoreViewHolder>{

    private static final String TAG = MoreAdapter.class.getSimpleName();

    private Context context;
    private List<Datums> playlists;

    public MoreAdapter(Context context, List<Datums> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @Override
    public MoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new MoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoreViewHolder holder, int position) {
        final Datums playlistObject = playlists.get(position);
        holder.sentence.setText(HummingUtils.getSentenceByMode(playlistObject, context));
        holder.vtitle.setText(HummingUtils.getTitleByMode(playlistObject, context));
        holder.time.setText(HummingUtils.getTime(playlistObject, context));
        if (HummingUtils.isEmpty(holder.time)) {
            holder.time.setVisibility(View.GONE);
        }

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

    public class MoreViewHolder extends RecyclerView.ViewHolder{

        public TextView sentence;
        public TextView vtitle;
        public TextView time;
        public ImageView thumbnail;

        public MoreViewHolder(View itemView) {
            super(itemView);

            sentence = (TextView)itemView.findViewById(R.id.sentence);
            vtitle = (TextView)itemView.findViewById(R.id.vtitle);
            time = (TextView)itemView.findViewById(R.id.time);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
        }
    }
}
