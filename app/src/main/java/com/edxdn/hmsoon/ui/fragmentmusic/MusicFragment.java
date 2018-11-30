package com.edxdn.hmsoon.ui.fragmentmusic;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.edxdn.hmsoon.R;
import com.edxdn.hmsoon.databinding.FragmentExploreBinding;
import com.edxdn.hmsoon.api.APIClient;
import com.edxdn.hmsoon.api.APIInterface;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.api.SearchResource;
import com.edxdn.hmsoon.ui.data.DataTypeMusicFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MusicFragment extends Fragment {

    private FragmentExploreBinding binding;
    private MusicFragmentAdapter mAdapter;
    private List<Datums> rankingList, recentList, motherGooseList, recommendList;
    private HashMap<String, List<Datums>> dataset;
    private APIInterface apiInterface;

    private CompositeDisposable disposable = new CompositeDisposable();
    private static final String TAG = MusicFragment.class.getSimpleName();

    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_explore, container, false);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        dataset = new HashMap<String, List<Datums>>();

        rankingList = new ArrayList<>();
        recentList = new ArrayList<>();
        motherGooseList = new ArrayList<>();
        recommendList = new ArrayList<>();

        // 아덥터
        mAdapter = new MusicFragmentAdapter(getActivity(), dataset);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rv.setLayoutManager(layoutManager);
        binding.rv.setAdapter(mAdapter);

        getDataRetrofit();

        return binding.getRoot();
    }

    private void getDataRetrofit() {
        getDataRanking(1, "");
        getDataRecent(1, "");
        getDataMotherGoose(1, "");
        getDataChat(1, "");
    }

    // 인기순위
    public void getDataRanking(int current_page, String sort) {
        disposable.add(
                apiInterface.getPopular(current_page+"", "", sort)
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SearchResource>() {
                            @Override
                            public void onNext(SearchResource searchResource) {
                                if (searchResource != null && searchResource.hits != null) {
                                    rankingList.addAll(searchResource.hits.hits);
                                    dataset.put(DataTypeMusicFragment.MUSIC_RANKING_TYPE, rankingList);
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
    }

    //최근 음악
    public void getDataRecent(int current_page, String sort) {
        disposable.add(
                apiInterface.getContents(current_page+"", "youtube_mothergoose")
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SearchResource>() {
                            @Override
                            public void onNext(SearchResource searchResource) {
                                if (searchResource != null && searchResource.hits != null) {
                                    recentList.addAll(searchResource.hits.hits);
                                    dataset.put(DataTypeMusicFragment.MUSIC_RECENT_TYPE, recentList);
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
    }

    //추천 동요
    public void getDataMotherGoose(int current_page, String sort) {
        disposable.add(
                apiInterface.getContents(current_page+"", "youtube_mothergoose")
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SearchResource>() {
                            @Override
                            public void onNext(SearchResource searchResource) {
                                if (searchResource != null && searchResource.hits != null) {
                                    motherGooseList.addAll(searchResource.hits.hits);
                                    dataset.put(DataTypeMusicFragment.MUSIC_MOTHERGOOSE_TYPE, motherGooseList);
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
    }

    //추천 음악
    public void getDataChat(int current_page, String interest) {
        disposable.add(
                apiInterface.getPopular(current_page+"", "", interest)
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SearchResource>() {
                            @Override
                            public void onNext(SearchResource searchResource) {
                                if (searchResource != null && searchResource.hits != null) {
                                    recommendList.addAll(searchResource.hits.hits);
                                    dataset.put(DataTypeMusicFragment.MUSIC_RECOMMEND_TYPE, recommendList);
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
    }
}