package com.test.english.ui.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.exam.english.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.test.english.api.Datums;
import com.test.english.entities.DataTypeMusicFragment;
import com.test.english.ui.data.SingleItemModel;
import com.test.english.util.HummingUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = MusicFragmentAdapter.class.getSimpleName();

    private Context context;
    private List<Datums> playlists;
    private ArrayList<List<Datums>> dataset;

    public MusicFragmentAdapter(Context context, ArrayList<List<Datums>> dataset) {
        this.context = context;
        this.dataset = dataset;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case DataTypeMusicFragment.IMAGE_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
                return new ImageTypeViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        List<Datums> object = dataset.get(position);
        //final Datums playlistObject = playlists.get(position);
        ArrayList<SingleItemModel> singleItem = new ArrayList<SingleItemModel>();

        if (object != null) {
            if (object.get(0).type.equals("video")) {
                for (Datums datas : object) {
                    //(((ImageTypeViewHolder) holder).sentence.setText(HummingUtils.getSentenceByMode(datas, context));
                    //(((ImageTypeViewHolder) holder).vtitle.setText(HummingUtils.getTitleByMode(datas, context));

                    singleItem.add(new SingleItemModel("", "", HummingUtils.IMAGE_PATH + datas.source.get(HummingUtils.ElasticField.THUMBNAIL_URL), HummingUtils.getTime(datas, context)));

                    /*((ImageTypeViewHolder) holder).time.setText(HummingUtils.getTime(datas, context));

                    if (HummingUtils.isEmpty(((ImageTypeViewHolder) holder).time)) {
                        ((ImageTypeViewHolder) holder).time.setVisibility(View.GONE);
                    }*/

                    /*Picasso.with(context).load(HummingUtils.IMAGE_PATH + datas.source.get(HummingUtils.ElasticField.THUMBNAIL_URL)).into(((ImageTypeViewHolder) holder).thumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            ((ImageTypeViewHolder) holder).thumbnail.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            ((ImageTypeViewHolder) holder).thumbnail.setVisibility(View.VISIBLE);
                        }
                    });*/
                }
                SectionListDataAdapter itemListDataAdapter = new SectionListDataAdapter(context, singleItem);

                ((ImageTypeViewHolder) holder).recycler_view_list.setHasFixedSize(true);
                ((ImageTypeViewHolder) holder).recycler_view_list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                ((ImageTypeViewHolder) holder).recycler_view_list.setAdapter(itemListDataAdapter);
                ((ImageTypeViewHolder) holder).recycler_view_list.setNestedScrollingEnabled(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataset.get(position).get(0).type.equals("video")) {
            return DataTypeMusicFragment.IMAGE_TYPE;
        }
        return -1;
    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {

        protected TextView itemTitle;
        protected RecyclerView recycler_view_list;
        protected Button btnMore;

        public ImageTypeViewHolder(View itemView) {
            super(itemView);
            this.itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) itemView.findViewById(R.id.recycler_view_list);
            this.btnMore= (Button) itemView.findViewById(R.id.btnMore);
        }
    }
}
