package com.edxdn.hmsoon.ui.fragmentcommon;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.edxdn.hmsoon.R;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("ValidFragment")
public class WatchedFragment extends Fragment implements MainActivity.onKeyBackPressedListener {
    private String Tag = "WatchedFragment";
    private String email = "";

    private RecyclerView playlisRecyclerView;
    private APIInterface apiInterface;

    private List<Datums> datumList;
    private WatchedAdapter mAdapter;
    private Handler mHandler;

    private MainActivity mainActivity;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CompositeDisposable disposable = new CompositeDisposable();

    public WatchedFragment() {
    }

    public static WatchedFragment newInstance() {
        return new WatchedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_favorite, container, false);

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
                getData(current_page+1, email);
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
        email = mainActivity.getEmailInfo();
        mHandler = new Handler(){
            public void handleMessage(Message msg){
                Datums datums = datumList.get(msg.what);
                mainActivity.setVideoUrl(datums);
            }
        };

        mAdapter = new WatchedAdapter(getActivity(), datumList);
        playlisRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.SEARCH_CHECK = false;
                datumList.clear();
                mAdapter.notifyDataSetChanged();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData(1, email);
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
        );

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                getData(1, email);
            }
        }, 0);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getData(int current_page, String email) {

        disposable.add(
                apiInterface.getWatched(current_page + "", email)
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SearchResource>() {
                            @Override
                            public void onNext(SearchResource searchResource) {
                                if (searchResource != null && searchResource.hits != null) {
                                    datumList.addAll(searchResource.hits.hits);
                                    //MyCustomApplication.getMainInstance().setFavoriteList(datumList);
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                            }
                        })
        );

       /*if(!email.equals("")){
           Call<SearchResource> call = apiInterface.getWatched(current_page+"", email);
           call.enqueue(new Callback<SearchResource>() {
               @Override
               public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                   SearchResource resource = response.body();
                   if(resource != null && resource.hits != null){
                       datumList.addAll(resource.hits.hits);
                   }
                   mAdapter.notifyDataSetChanged();
               }

               @Override
               public void onFailure(Call<SearchResource> call, Throwable t) {
                   Log.d("test","================================================2"+t.getMessage());
                   call.cancel();
               }
           });
       }*/

    }

    @Override
    public void onBack() {
        mainActivity.setOnKeyBackPressedListener(null);
        mainActivity.onBackPressed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    //    ((MainActivity) getActivity()).setOnKeyBackPressedListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
