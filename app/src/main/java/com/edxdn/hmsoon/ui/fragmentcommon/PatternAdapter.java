package com.edxdn.hmsoon.ui.fragmentcommon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.exam.english.R;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.util.HummingUtils;
import java.util.List;

public class PatternAdapter extends RecyclerView.Adapter<PatternAdapter.PatternViewHolder>{

    private static final String TAG = PatternAdapter.class.getSimpleName();

    private Context context;
    private List<Datums> playlists;

    public PatternAdapter(Context context, List<Datums> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @Override
    public PatternViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pattern, parent, false);
        return new PatternViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PatternViewHolder holder, int position) {
        final Datums playlistObject = playlists.get(position);
        holder.sentence.setText(playlistObject.source.get(HummingUtils.ElasticField.PATTERN).toString());
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class PatternViewHolder extends RecyclerView.ViewHolder{

        public TextView sentence;

        public PatternViewHolder(View itemView) {
            super(itemView);
            sentence = (TextView)itemView.findViewById(R.id.sentence);
        }
    }

}
