package com.edxdn.hmsoon.ui.fragmentcommon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edxdn.hmsoon.ui.data.DataTypeMusicFragment;
import com.edxdn.hmsoon.R;
import com.edxdn.hmsoon.api.APIClient;
import com.edxdn.hmsoon.api.APIInterface;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.api.SearchResource;
import com.edxdn.hmsoon.application.MyCustomApplication;
import com.edxdn.hmsoon.ui.adapter.EndlessRecyclerOnScrollListener;
import com.edxdn.hmsoon.ui.adapter.RecyclerItemClickListener;
import com.edxdn.hmsoon.ui.adapter.SpacesItemDecoration;
import com.edxdn.hmsoon.ui.main.MainActivity;
import com.edxdn.hmsoon.util.HummingUtils;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class PatternFragment extends Fragment implements MainActivity.onKeyBackPressedListener {

    private String TAG = MyCustomApplication.TAG+"PopularFragment";
    private String searchSentence;
    private RecyclerView playlisRecyclerView;
    private APIInterface apiInterface;
    private List<Datums> datumList;
    private PatternAdapter mAdapter;
    private Handler mHandler;
    private MainActivity mainActivity;
    private CompositeDisposable disposable = new CompositeDisposable();

    public PatternFragment() {
    }

    public static PatternFragment newInstance() {
        return new PatternFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pattern, container, false);

        datumList = new ArrayList<>();
        playlisRecyclerView = (RecyclerView)view.findViewById(R.id.your_play_list);

        SpacesItemDecoration decoration = new SpacesItemDecoration(20);
        playlisRecyclerView.addItemDecoration(decoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        playlisRecyclerView.setLayoutManager(layoutManager);

        playlisRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                //getData(current_page+1, "");
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

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                Datums datums = datumList.get(msg.what);
                MyCustomApplication.getMainInstance().onClickItems("sentences", datums.source.get(HummingUtils.ElasticField.PATTERN).toString());
            }
        };

        mAdapter = new PatternAdapter(getActivity(), datumList);
        playlisRecyclerView.setAdapter(mAdapter);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                getData(1, "");
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

    public void getData(int current_page, String pattern) {
        disposable.add(
                apiInterface.getPatterns(current_page + "", "")
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SearchResource>() {
                            @Override
                            public void onNext(SearchResource searchResource) {
                                if (searchResource != null && searchResource.hits != null) {
                                    datumList.addAll(searchResource.hits.hits);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                Log.e(TAG, "onComplete: " + "getDataSentence");
                            }
                        })
        );
        /*Call<SearchResource> call = apiInterface.getPatterns(current_page + "", pattern);
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
        ((MainActivity)getActivity()).getBottomNavigation().setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity)getActivity()).getBottomNavigation().setVisibility(View.VISIBLE);
    }
}
