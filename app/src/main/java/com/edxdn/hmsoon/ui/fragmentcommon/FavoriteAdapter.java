package com.edxdn.hmsoon.ui.fragmentcommon;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.edxdn.hmsoon.R;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.util.HummingUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>{

    private static final String TAG = FavoriteAdapter.class.getSimpleName();

    private Context context;
    private List<Datums> playlists;

    public FavoriteAdapter(Context context, List<Datums> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favirite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavoriteViewHolder holder, int position) {
        final Datums playlistObject = playlists.get(position);
        holder.sentence.setText(HummingUtils.getSentenceByMode(playlistObject, context));
        holder.vtitle.setText(HummingUtils.getTitleByMode(playlistObject, context));
        holder.time.setText(HummingUtils.getTime(playlistObject, context));
        if (HummingUtils.isEmpty(holder.time)) {
            holder.time.setVisibility(View.GONE);
        }
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

    public class FavoriteViewHolder extends RecyclerView.ViewHolder{

        public TextView sentence;
        public TextView vtitle;
        public TextView time;
        public ImageView thumbnail;

        public FavoriteViewHolder(View itemView) {
            super(itemView);

            sentence = (TextView)itemView.findViewById(R.id.sentence);
            vtitle = (TextView)itemView.findViewById(R.id.vtitle);
            time = (TextView)itemView.findViewById(R.id.time);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
        }
    }
}
