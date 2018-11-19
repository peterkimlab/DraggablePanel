package com.edxdn.hmsoon.ui.fragmentmusic;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.edxdn.hmsoon.ui.adapter.SpacesItemDecoration;
import com.edxdn.hmsoon.ui.data.ExploreFragmentItemModel;
import com.edxdn.hmsoon.ui.fragmentexplore.ExploreFragmentAdapter;
import com.exam.english.R;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.ui.data.DataTypeMusicFragment;
import com.edxdn.hmsoon.util.HummingUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ExploreFragmentAdapter.class.getSimpleName();
    private Context context;
    private HashMap<String, List<Datums>> dataset;

    private final int DECORATE_PADDING = 5;

    public static final int RANKING_TYPE = 0;
    public static final int RECENT_TYPE = 1;
    public static final int MOTHER_GOOSE_TYPE = 2;
    public static final int RECOMMEND_TYPE = 3;

    public MusicFragmentAdapter(Context context, HashMap<String, List<Datums>> dataset) {
        this.context = context;
        this.dataset = dataset;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return RANKING_TYPE;
            case 1:
                return RECENT_TYPE;
            case 2:
                return MOTHER_GOOSE_TYPE;
            case 3:
                return RECOMMEND_TYPE;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case RANKING_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_music_ranking, parent, false);
                return new MusicFragmentAdapter.RankingViewHolder(view);
            case RECENT_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_music_sub_title, parent, false);
                return new MusicFragmentAdapter.RecentViewHolder(view);
            case MOTHER_GOOSE_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_explore_standard, parent, false);
                return new MusicFragmentAdapter.MotherGooseViewHolder(view);
            case RECOMMEND_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_explore_standard, parent, false);
                return new MusicFragmentAdapter.RecommendViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ArrayList<ExploreFragmentItemModel> singleItem = new ArrayList<ExploreFragmentItemModel>();
        List<Datums> dataList;
        MusicFragmentDataAdapter itemListDataAdapter = null;

        SpacesItemDecoration decoration = new SpacesItemDecoration(DECORATE_PADDING);

        switch (position) {
            case RANKING_TYPE:
                dataList = dataset.get(DataTypeMusicFragment.MUSIC_RANKING_TYPE);
                if (dataList != null) {
                    for (Datums datas : dataList) {
                        singleItem.add(new ExploreFragmentItemModel(RANKING_TYPE, HummingUtils.getTitle(datas, context), HummingUtils.IMAGE_PATH + datas.source.get(HummingUtils.ElasticField.THUMBNAIL_URL), HummingUtils.getTime(datas, context), HummingUtils.getSentenceByMode(datas, context)));
                    }
                    ((MusicFragmentAdapter.RankingViewHolder) holder).recycler_view_list.addItemDecoration(new MusicFragmentAdapter.GridSpacingItemDecoration(3, dpToPx(2), true));
                    ((MusicFragmentAdapter.RankingViewHolder) holder).recycler_view_list.setLayoutManager(new GridLayoutManager(context, 3));
                    ((MusicFragmentAdapter.RankingViewHolder) holder).recycler_view_list.setHasFixedSize(true);
                    itemListDataAdapter = new MusicFragmentDataAdapter(context, singleItem, dataList);
                    ((MusicFragmentAdapter.RankingViewHolder) holder).recycler_view_list.setAdapter(itemListDataAdapter);
                    ((MusicFragmentAdapter.RankingViewHolder) holder).recycler_view_list.setNestedScrollingEnabled(false);
                }
                ((MusicFragmentAdapter.RankingViewHolder) holder).itemMainTitle.setText("인기순위");
                break;
            case RECENT_TYPE:
                dataList = dataset.get(DataTypeMusicFragment.MUSIC_RECENT_TYPE);
                if (dataList != null) {
                    for (Datums datas : dataList) {
                        singleItem.add(new ExploreFragmentItemModel(RECENT_TYPE, HummingUtils.getTitle(datas, context), HummingUtils.IMAGE_PATH + datas.source.get(HummingUtils.ElasticField.THUMBNAIL_URL), HummingUtils.getTime(datas, context), HummingUtils.getSentenceByMode(datas, context)));
                    }
                    ((MusicFragmentAdapter.RecentViewHolder) holder).recycler_view_list.addItemDecoration(decoration);
                    ((MusicFragmentAdapter.RecentViewHolder) holder).recycler_view_list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    ((MusicFragmentAdapter.RecentViewHolder) holder).recycler_view_list.setHasFixedSize(true);
                    itemListDataAdapter = new MusicFragmentDataAdapter(context, singleItem, dataList);
                    ((MusicFragmentAdapter.RecentViewHolder) holder).recycler_view_list.setAdapter(itemListDataAdapter);
                    ((MusicFragmentAdapter.RecentViewHolder) holder).recycler_view_list.setNestedScrollingEnabled(false);
                }
                ((MusicFragmentAdapter.RecentViewHolder) holder).itemMainTitle.setText("최근음악");
                break;
            case MOTHER_GOOSE_TYPE:
                dataList = dataset.get(DataTypeMusicFragment.MUSIC_MOTHERGOOSE_TYPE);
                if (dataList != null) {
                    for (Datums datas : dataList) {
                        singleItem.add(new ExploreFragmentItemModel(MOTHER_GOOSE_TYPE, HummingUtils.getTitle(datas, context), HummingUtils.IMAGE_PATH + datas.source.get(HummingUtils.ElasticField.THUMBNAIL_URL), HummingUtils.getTime(datas, context), HummingUtils.getSentenceByMode(datas, context)));
                    }
                    ((MusicFragmentAdapter.MotherGooseViewHolder) holder).recycler_view_list.addItemDecoration(new MusicFragmentAdapter.GridSpacingItemDecoration(2, dpToPx(5), true));
                    ((MusicFragmentAdapter.MotherGooseViewHolder) holder).recycler_view_list.setLayoutManager(new GridLayoutManager(context, 2));
                    ((MusicFragmentAdapter.MotherGooseViewHolder) holder).recycler_view_list.setHasFixedSize(true);
                    itemListDataAdapter = new MusicFragmentDataAdapter(context, singleItem, dataList);
                    ((MusicFragmentAdapter.MotherGooseViewHolder) holder).recycler_view_list.setAdapter(itemListDataAdapter);
                    ((MusicFragmentAdapter.MotherGooseViewHolder) holder).recycler_view_list.setNestedScrollingEnabled(false);
                }
                ((MusicFragmentAdapter.MotherGooseViewHolder) holder).itemMainTitle.setText("추천동요");
                break;
            case RECOMMEND_TYPE:
                dataList = dataset.get(DataTypeMusicFragment.MUSIC_RECOMMEND_TYPE);
                if (dataList != null) {
                    for (Datums datas : dataList) {
                        singleItem.add(new ExploreFragmentItemModel(RECOMMEND_TYPE, datas.source.get(HummingUtils.ElasticField.STYPE).toString(),"","", datas.source.get(HummingUtils.ElasticField.TITLE).toString()));
                    }
                    ((MusicFragmentAdapter.RecommendViewHolder) holder).recycler_view_list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    ((MusicFragmentAdapter.RecommendViewHolder) holder).recycler_view_list.setHasFixedSize(true);
                    itemListDataAdapter = new MusicFragmentDataAdapter(context, singleItem, dataList);
                    ((MusicFragmentAdapter.RecommendViewHolder) holder).recycler_view_list.setAdapter(itemListDataAdapter);
                    ((MusicFragmentAdapter.RecommendViewHolder) holder).recycler_view_list.setNestedScrollingEnabled(false);
                }
                ((MusicFragmentAdapter.RecommendViewHolder) holder).itemMainTitle.setText("추천음악");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder{

        protected TextView itemMainTitle;
        protected TextView itemTitle;
        protected RecyclerView recycler_view_list;
        protected Button btnMore;

        public RankingViewHolder(View itemView) {
            super(itemView);
            this.itemMainTitle = (TextView) itemView.findViewById(R.id.itemMainTitle);
            this.itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) itemView.findViewById(R.id.recycler_view_list);
            this.btnMore= (Button) itemView.findViewById(R.id.btnMore);
        }
    }

    public static class RecentViewHolder extends RecyclerView.ViewHolder {

        protected TextView itemMainTitle;
        protected TextView itemTitle;
        protected RecyclerView recycler_view_list;
        protected Button btnMore;

        public RecentViewHolder(View itemView) {
            super(itemView);
            this.itemMainTitle = (TextView) itemView.findViewById(R.id.itemMainTitle);
            this.itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) itemView.findViewById(R.id.recycler_view_list);
        }
    }

    public class MotherGooseViewHolder extends RecyclerView.ViewHolder {

        protected TextView itemMainTitle;
        protected TextView itemTitle;
        protected RecyclerView recycler_view_list;
        protected Button btnMore;

        public MotherGooseViewHolder(View itemView) {
            super(itemView);
            this.itemMainTitle = (TextView) itemView.findViewById(R.id.itemMainTitle);
            this.itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) itemView.findViewById(R.id.recycler_view_list);
            this.btnMore= (Button) itemView.findViewById(R.id.btnMore);
        }
    }

    public static class RecommendViewHolder extends RecyclerView.ViewHolder{

        protected TextView itemMainTitle;
        protected TextView itemTitle;
        protected RecyclerView recycler_view_list;
        protected Button btnMore;

        public RecommendViewHolder(View itemView) {
            super(itemView);
            this.itemMainTitle = (TextView) itemView.findViewById(R.id.itemMainTitle);
            this.itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) itemView.findViewById(R.id.recycler_view_list);
            this.btnMore= (Button) itemView.findViewById(R.id.btnMore);
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}