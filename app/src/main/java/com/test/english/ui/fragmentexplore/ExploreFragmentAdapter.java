package com.test.english.ui.fragmentexplore;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.exam.english.R;
import com.test.english.api.Datums;
import com.test.english.ui.adapter.SpacesItemDecoration;
import com.test.english.ui.data.DataTypeMusicFragment;
import com.test.english.ui.data.ExploreFragmentItemModel;
import com.test.english.util.HummingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExploreFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ExploreFragmentAdapter.class.getSimpleName();
    private Context context;
    private HashMap<String, List<Datums>> dataset;

    public static final int SENTENCE_TYPE = 1;
    public static final int PATTERN_TYPE = 2;

    public ExploreFragmentAdapter(Context context, HashMap<String, List<Datums>> dataset) {
        this.context = context;
        this.dataset = dataset;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return SENTENCE_TYPE;
            case 1:
                return PATTERN_TYPE;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case SENTENCE_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_explore_subtitle, parent, false);
                return new SentenceViewHolder(view);
            case PATTERN_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_explore_subtitle, parent, false);
                return new PatternViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ArrayList<ExploreFragmentItemModel> singleItem = new ArrayList<ExploreFragmentItemModel>();
        List<Datums> dataList;
        ExploreFragmentDataAdapter itemListDataAdapter;

        SpacesItemDecoration decoration = new SpacesItemDecoration(10);

        switch (position) {
            case 0:
                dataList = dataset.get(DataTypeMusicFragment.EXPLORE_SENTENCE_TYPE);
                ((SentenceViewHolder) holder).itemTitle.setText("추천문장");
                if (dataList != null) {
                    for (Datums datas : dataList) {
                        singleItem.add(new ExploreFragmentItemModel("","","", datas.source.get(HummingUtils.ElasticField.PATTERN).toString()));
                    }

                    itemListDataAdapter = new ExploreFragmentDataAdapter(context, singleItem, dataList);

                    ((SentenceViewHolder) holder).recycler_view_list.addItemDecoration(decoration);
                    ((SentenceViewHolder) holder).recycler_view_list.setHasFixedSize(true);
                    ((SentenceViewHolder) holder).recycler_view_list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    ((SentenceViewHolder) holder).recycler_view_list.setAdapter(itemListDataAdapter);
                    ((SentenceViewHolder) holder).recycler_view_list.setNestedScrollingEnabled(false);
                }
                break;
            case 1:
                dataList = dataset.get(DataTypeMusicFragment.EXPLORE_PATTERN_TYPE);
                ((PatternViewHolder) holder).itemTitle.setText("추천패턴");
                if (dataList != null) {
                    for (Datums datas : dataList) {
                        singleItem.add(new ExploreFragmentItemModel("","","", datas.source.get(HummingUtils.ElasticField.PATTERN).toString()));
                    }

                    itemListDataAdapter = new ExploreFragmentDataAdapter(context, singleItem, dataList);

                    ((PatternViewHolder) holder).recycler_view_list.addItemDecoration(decoration);
                    ((PatternViewHolder) holder).recycler_view_list.setHasFixedSize(true);
                    ((PatternViewHolder) holder).recycler_view_list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    ((PatternViewHolder) holder).recycler_view_list.setAdapter(itemListDataAdapter);
                    ((PatternViewHolder) holder).recycler_view_list.setNestedScrollingEnabled(false);
                }
                break;

        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class SentenceViewHolder extends RecyclerView.ViewHolder{

        protected TextView itemTitle;
        protected RecyclerView recycler_view_list;
        protected Button btnMore;

        public SentenceViewHolder(View itemView) {
            super(itemView);
            this.itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) itemView.findViewById(R.id.recycler_view_list);
            this.btnMore= (Button) itemView.findViewById(R.id.btnMore);
        }
    }

    public static class PatternViewHolder extends RecyclerView.ViewHolder{

        protected TextView itemTitle;
        protected RecyclerView recycler_view_list;
        protected Button btnMore;

        public PatternViewHolder(View itemView) {
            super(itemView);
            this.itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) itemView.findViewById(R.id.recycler_view_list);
            this.btnMore= (Button) itemView.findViewById(R.id.btnMore);
        }
    }
}
