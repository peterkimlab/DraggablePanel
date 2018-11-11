package com.test.english.ui.searchfragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.exam.english.R;
import com.test.english.api.Datums;
import com.test.english.util.HummingUtils;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHelperViewHolder>{

    private static final String TAG = SearchAdapter.class.getSimpleName();

    private Context context;
    private List<Datums> playlists;

    public SearchAdapter(Context context, List<Datums> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @Override
    public SearchHelperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_searchhelper, parent, false);
        return new SearchHelperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchHelperViewHolder holder, int position) {
        final Datums playlistObject = playlists.get(position);
        holder.sentence.setText(playlistObject.source.get(HummingUtils.ElasticField.TEXT_EN).toString());
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class SearchHelperViewHolder extends RecyclerView.ViewHolder{

        public TextView sentence;

        public SearchHelperViewHolder(View itemView) {
            super(itemView);

            sentence = (TextView)itemView.findViewById(R.id.sentence);
        }
    }
}
