package com.test.english.ui.searchfragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.exam.english.R;
import com.test.english.api.Datums;
import com.test.english.util.HummingUtils;
import java.util.List;

public class SearchBeforeHandAdapter extends RecyclerView.Adapter<SearchBeforeHandAdapter.SearchViewHolder>{

    private static final String TAG = SearchBeforeHandAdapter.class.getSimpleName();

    private Context mContext;
    private List<Datums> datums;

    public SearchBeforeHandAdapter(Context context, List<Datums> datums) {
        this.mContext = context;
        this.datums = datums;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_searchhelper, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, int position) {

        Datums datum = datums.get(position);

        Glide.with(mContext)
                .load(HummingUtils.IMAGE_PATH + datum.source.get(HummingUtils.ElasticField.THUMBNAIL_URL))
                .into(((SearchViewHolder) holder).itemImage);
        ((SearchViewHolder) holder).tvSentence.setText(datum.source.get(HummingUtils.ElasticField.TEXT_EN).toString());
    }

    @Override
    public int getItemCount() {
        return datums.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        ImageView itemImage;
        TextView tvTime;
        TextView tvSentence;
        public SearchViewHolder(View view) {
            super(view);
            this.itemImage = (ImageView) view.findViewById(R.id.thumbnail);
            this.tvTitle = (TextView) view.findViewById(R.id.vtitle);
            this.tvSentence = (TextView) view.findViewById(R.id.sentence);
        }
    }
}
