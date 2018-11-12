package com.test.english.ui.searchfragment;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exam.english.R;
import com.test.english.api.APIClient;
import com.test.english.api.APIInterface;
import com.test.english.api.Datums;
import com.test.english.api.SearchResource;
import com.test.english.application.MyCustomApplication;
import com.test.english.util.HummingUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchBeforeHandFragment extends Fragment {

    private APIInterface apiInterface;
    private List<Datums> datumList;
    private RecyclerView recyclerView;
    private SearchBeforeHandAdapter mSearchAdapter;
    private TextView tvSearch;

    public static SearchBeforeHandFragment newInstance() {
        return new SearchBeforeHandFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_before_hand, container, false);

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
        }
        datumList = new ArrayList<>();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        getData("hey");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        tvSearch = (TextView) view.findViewById(R.id.tvSearch);

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setHasFixedSize(true);
        mSearchAdapter = new SearchBeforeHandAdapter(getContext(), datumList);
        recyclerView.setAdapter(mSearchAdapter);

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCustomApplication.getMainInstance().searchSentencePopup("search");
            }
        });

        return view;
    }

    public void getData(String sentence) {
        Call<SearchResource> call = apiInterface.doSearchDataList(1 + "", sentence);
        //Call<PostResource> call = apiInterface.postSearchHistory(email, sentence);
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();

                datumList.clear();
                for (int i = 0; i< resource.hits.hits.size(); i++) {
                    boolean check = true;
                    for (int j = 0; j< datumList.size(); j++) {
                        if (datumList.get(j).source.get(HummingUtils.ElasticField.TEXT_EN).toString().equals(resource.hits.hits.get(i).source.get(HummingUtils.ElasticField.TEXT_EN).toString())) {
                            check = false;
                            break;
                        }
                    }
                    if (check) {
                        datumList.add(resource.hits.hits.get(i));
                    }
                }
                mSearchAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
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

    private int dpToPx(int dp) {
        Resources r = getContext().getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
