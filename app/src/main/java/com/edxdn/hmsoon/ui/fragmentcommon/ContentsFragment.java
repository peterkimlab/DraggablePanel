package com.edxdn.hmsoon.ui.fragmentcommon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edxdn.hmsoon.R;
import com.edxdn.hmsoon.api.APIClient;
import com.edxdn.hmsoon.api.APIInterface;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.application.MyCustomApplication;
import com.edxdn.hmsoon.ui.adapter.EndlessRecyclerOnScrollListener;
import com.edxdn.hmsoon.ui.adapter.RecyclerItemClickListener;
import com.edxdn.hmsoon.ui.adapter.SpacesItemDecoration;
import com.edxdn.hmsoon.ui.main.MainActivity;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class ContentsFragment extends Fragment implements MainActivity.onKeyBackPressedListener {

    private String TAG = MyCustomApplication.TAG + "ContentsFragment";
    private String searchSentence;
    private RecyclerView playlisRecyclerView;
    private APIInterface apiInterface;
    private List<Datums> datumList;
    private ContentsAdapter mAdapter;
    private Handler mHandler;
    private MainActivity mainActivity;

    public ContentsFragment() {
    }

    public static ContentsFragment newInstance() {
        return new ContentsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_more, container, false);

        datumList = new ArrayList<>();
        playlisRecyclerView = (RecyclerView)view.findViewById(R.id.your_play_list);

        SpacesItemDecoration decoration = new SpacesItemDecoration(20);
        playlisRecyclerView.addItemDecoration(decoration);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                playlisRecyclerView.getContext(),  LinearLayoutManager.VERTICAL
        );
        playlisRecyclerView.addItemDecoration(mDividerItemDecoration);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        playlisRecyclerView.setLayoutManager(layoutManager);
        playlisRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getData(current_page+1, searchSentence);
            }

            @Override
            public void onScrolledToTop(int current_page) {
            }

            @Override
            public void onScrolledUpDown(int dy) {
            }
        });
        playlisRecyclerView.setHasFixedSize(true);
        playlisRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), playlisRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
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

        mAdapter = new ContentsAdapter(getActivity(), datumList);
        playlisRecyclerView.setAdapter(mAdapter);
        searchSentence = "";

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                getData(1, searchSentence);
            }
        }, 100);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override public void run() {
                mainActivity.showToolbar();
            }
        }, 200);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getData(int current_page, String sentence) {
        /*Call<SearchResource> call = apiInterface.getContents(current_page+"", "youtube_mothergoose");
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                datumList.addAll(resource.hits.hits);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                Log.e(TAG,t.getMessage());
                call.cancel();
            }
        });*/
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
