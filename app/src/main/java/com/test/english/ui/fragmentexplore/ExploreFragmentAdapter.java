package com.test.english.ui.fragmentexplore;

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
import android.widget.Toast;

import com.exam.english.R;
import com.test.english.api.Datums;
import com.test.english.application.MyCustomApplication;
import com.test.english.ui.adapter.SpacesItemDecoration;
import com.test.english.ui.data.DataTypeMusicFragment;
import com.test.english.ui.data.ExploreFragmentItemModel;
import com.test.english.ui.fragmentmusic.MusicFragmentAdapter;
import com.test.english.util.HummingUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExploreFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ExploreFragmentAdapter.class.getSimpleName();
    private Context context;
    private HashMap<String, List<Datums>> dataset;

    private final int DECORATE_PADDING = 5;

    public static final int SENTENCE_TYPE = 0;
    public static final int PATTERN_TYPE = 1;
    public static final int POPULAR_TYPE = 2;
    //public static final int STUDIED_SENTENCE_TYPE = 3;
    public static final int CHAT_TYPE = 3;

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
            case 2:
                return POPULAR_TYPE;
            case 3:
                return CHAT_TYPE;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case SENTENCE_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_explore_main_title, parent, false);
                return new SentenceViewHolder(view);
            case PATTERN_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_explore_sub_title, parent, false);
                return new PatternViewHolder(view);
            case POPULAR_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_explore_standard, parent, false);
                return new PopularViewHolder(view);
            case CHAT_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_explore_standard, parent, false);
                return new ChatViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ArrayList<ExploreFragmentItemModel> singleItem = new ArrayList<ExploreFragmentItemModel>();
        List<Datums> dataList;
        ExploreFragmentDataAdapter itemListDataAdapter = null;

        switch (position) {
            case SENTENCE_TYPE:
                dataList = dataset.get(DataTypeMusicFragment.EXPLORE_SENTENCE_TYPE);
                if (dataList != null) {
                    for (Datums datas : dataList) {
                        singleItem.add(new ExploreFragmentItemModel(SENTENCE_TYPE, "","","", datas.source.get(HummingUtils.ElasticField.PATTERN).toString()));
                    }
                    ((SentenceViewHolder) holder).recycler_view_list.addItemDecoration(new SpacesItemDecoration(DECORATE_PADDING));
                    ((SentenceViewHolder) holder).recycler_view_list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    ((SentenceViewHolder) holder).recycler_view_list.setHasFixedSize(true);
                    itemListDataAdapter = new ExploreFragmentDataAdapter(context, singleItem, dataList);
                    ((SentenceViewHolder) holder).recycler_view_list.setAdapter(itemListDataAdapter);
                    ((SentenceViewHolder) holder).recycler_view_list.setNestedScrollingEnabled(false);

                    ((SentenceViewHolder) holder).btnMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MyCustomApplication.getMainInstance().onClickItems("sentences", "");
                        }
                    });
                }
                ((SentenceViewHolder) holder).itemMainTitle.setText("Today");
                ((SentenceViewHolder) holder).itemTitle.setText("추천문장");
                break;
            case PATTERN_TYPE:
                dataList = dataset.get(DataTypeMusicFragment.EXPLORE_PATTERN_TYPE);
                if (dataList != null) {
                    for (Datums datas : dataList) {
                        singleItem.add(new ExploreFragmentItemModel(PATTERN_TYPE,"",HummingUtils.IMAGE_PATH + datas.source.get(HummingUtils.ElasticField.THUMBNAIL_URL),"", datas.source.get(HummingUtils.ElasticField.PATTERN).toString()));
                    }
                    ((PatternViewHolder) holder).recycler_view_list.addItemDecoration(new SpacesItemDecoration(DECORATE_PADDING));
                    ((PatternViewHolder) holder).recycler_view_list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    ((PatternViewHolder) holder).recycler_view_list.setHasFixedSize(true);
                    itemListDataAdapter = new ExploreFragmentDataAdapter(context, singleItem, dataList);
                    ((PatternViewHolder) holder).recycler_view_list.setAdapter(itemListDataAdapter);
                    ((PatternViewHolder) holder).recycler_view_list.setNestedScrollingEnabled(false);

                    ((PatternViewHolder) holder).btnMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MyCustomApplication.getMainInstance().onClickItems("patterns", "");
                        }
                    });

                }
                ((PatternViewHolder) holder).itemTitle.setText("추천패턴");
                break;
            case POPULAR_TYPE:
                dataList = dataset.get(DataTypeMusicFragment.POPULAR_TYPE);
                if (dataList != null) {
                    for (Datums datas : dataList) {
                        singleItem.add(new ExploreFragmentItemModel(POPULAR_TYPE, HummingUtils.getTitle(datas, context), HummingUtils.IMAGE_PATH + datas.source.get(HummingUtils.ElasticField.THUMBNAIL_URL), HummingUtils.getTime(datas, context), HummingUtils.getSentenceByMode(datas, context)));
                    }
                    ((PopularViewHolder) holder).recycler_view_list.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
                    ((PopularViewHolder) holder).recycler_view_list.setLayoutManager(new GridLayoutManager(context, 2));
                    ((PopularViewHolder) holder).recycler_view_list.setHasFixedSize(true);
                    itemListDataAdapter = new ExploreFragmentDataAdapter(context, singleItem, dataList);
                    ((PopularViewHolder) holder).recycler_view_list.setAdapter(itemListDataAdapter);
                    ((PopularViewHolder) holder).recycler_view_list.setNestedScrollingEnabled(false);
                }
                ((PopularViewHolder) holder).itemMainTitle.setText("인기영상");
                break;
            case CHAT_TYPE:
                dataList = dataset.get(DataTypeMusicFragment.CHAT_TYPE);
                if (dataList != null) {
                    for (Datums datas : dataList) {
                        singleItem.add(new ExploreFragmentItemModel(CHAT_TYPE, datas.source.get(HummingUtils.ElasticField.STYPE).toString(),"","", datas.source.get(HummingUtils.ElasticField.TITLE).toString()));
                    }
                    //((ChatViewHolder) holder).recycler_view_list.addItemDecoration(new SpacesItemDecoration(DECORATE_PADDING));
                    ((ChatViewHolder) holder).recycler_view_list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    ((ChatViewHolder) holder).recycler_view_list.setHasFixedSize(true);
                    itemListDataAdapter = new ExploreFragmentDataAdapter(context, singleItem, dataList);
                    ((ChatViewHolder) holder).recycler_view_list.setAdapter(itemListDataAdapter);
                    ((ChatViewHolder) holder).recycler_view_list.setNestedScrollingEnabled(false);
                }
                ((ChatViewHolder) holder).itemMainTitle.setText("채팅회화");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class SentenceViewHolder extends RecyclerView.ViewHolder{

        protected TextView itemMainTitle;
        protected TextView itemTitle;
        protected RecyclerView recycler_view_list;
        protected Button btnMore;

        public SentenceViewHolder(View itemView) {
            super(itemView);
            this.itemMainTitle = (TextView) itemView.findViewById(R.id.itemMainTitle);
            this.itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) itemView.findViewById(R.id.recycler_view_list);
            this.btnMore= (Button) itemView.findViewById(R.id.btnMore);
        }
    }

    public static class PatternViewHolder extends RecyclerView.ViewHolder {

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

    public class PopularViewHolder extends RecyclerView.ViewHolder {

        protected TextView itemMainTitle;
        protected TextView itemTitle;
        protected RecyclerView recycler_view_list;
        protected Button btnMore;

        public PopularViewHolder(View itemView) {
            super(itemView);
            this.itemMainTitle = (TextView) itemView.findViewById(R.id.itemMainTitle);
            this.itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            this.recycler_view_list = (RecyclerView) itemView.findViewById(R.id.recycler_view_list);
            this.btnMore= (Button) itemView.findViewById(R.id.btnMore);
        }
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        protected TextView itemMainTitle;
        protected TextView itemTitle;
        protected RecyclerView recycler_view_list;
        protected Button btnMore;

        public ChatViewHolder(View itemView) {
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