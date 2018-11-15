package com.edxdn.hmsoon.ui.searchfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.edxdn.hmsoon.util.ClearEditText;
import com.exam.english.R;
import com.edxdn.hmsoon.api.APIClient;
import com.edxdn.hmsoon.api.APIInterface;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.api.SearchResource;
import com.edxdn.hmsoon.ui.adapter.EndlessRecyclerOnScrollListener;
import com.edxdn.hmsoon.ui.adapter.RecyclerItemClickListener;
import com.edxdn.hmsoon.ui.adapter.SpacesItemDecoration;
import com.edxdn.hmsoon.ui.main.MainActivity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class SearchFragment extends Fragment implements MainActivity.onKeyBackPressedListener {
    private String Tag = "AnalyticsFragment";

    private RecyclerView recyclerView;
    private APIInterface apiInterface;

    private List<Datums> datumList;
    private SearchAdapter mAdapter;
    private Handler mHandler;

    private MainActivity mainActivity;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ClearEditText tvSearch;

    public SearchFragment() {
    }

    public static SearchBeforeHandFragment newInstance() {
        return new SearchBeforeHandFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        datumList = new ArrayList<>();
        recyclerView = (RecyclerView)view.findViewById(R.id.your_play_list);

        SpacesItemDecoration decoration = new SpacesItemDecoration(20);
        recyclerView.addItemDecoration(decoration);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),  LinearLayoutManager.VERTICAL
        );
        recyclerView.addItemDecoration(mDividerItemDecoration);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        /*recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getData(current_page+1, MainActivity.SEARCH_VALUE);
            }

            @Override
            public void onScrolledToTop(int current_page) {
            }

            @Override
            public void onScrolledUpDown(int dy) {
            }
        });*/
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Message message= Message.obtain();
                        message.what = position;
                        mHandler.sendMessage(message);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        mainActivity = (MainActivity) getActivity();

        mHandler = new Handler(){
            public void handleMessage(Message msg){
                Datums datums = datumList.get(msg.what);
                mainActivity.setVideoUrl(datums);
            }
        };

        tvSearch = view.findViewById(R.id.tvSearch);
        tvSearch.requestFocus();

        tvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getData(tvSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.SEARCH_CHECK = false;
                datumList.clear();
                mAdapter.notifyDataSetChanged();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData(1, MainActivity.SEARCH_VALUE);
                    }
                }, 500);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 800);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );*/

        mAdapter = new SearchAdapter(getActivity(), datumList);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getData(String sentence) {

        if (sentence.isEmpty()) {
            datumList.clear();
            mAdapter.notifyDataSetChanged();
            return;
        }

        Call<SearchResource> call = apiInterface.doSearchDataList(1 + "", sentence);
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if (resource != null && resource.hits != null) {
                    datumList.clear();
                    datumList.addAll(resource.hits.hits);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

    @Override
    public void onBack() {
        mainActivity.setOnKeyBackPressedListener(null);
        mainActivity.onBackPressed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
