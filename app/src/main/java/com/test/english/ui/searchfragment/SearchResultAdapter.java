package com.test.english.ui.searchfragment;


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

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder>{

    private static final String TAG = SearchResultAdapter.class.getSimpleName();

    private Context context;
    private List<Datums> playlists;

    public SearchResultAdapter(Context context, List<Datums> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, int position) {
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

    public class SearchViewHolder extends RecyclerView.ViewHolder{

        public TextView sentence;
        public TextView vtitle;
        public ImageView thumbnail;
        public TextView time;

        public SearchViewHolder(View itemView) {
            super(itemView);
            sentence = (TextView)itemView.findViewById(R.id.sentence);
            vtitle = (TextView)itemView.findViewById(R.id.vtitle);
            time = (TextView)itemView.findViewById(R.id.time);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
        }
    }
}
