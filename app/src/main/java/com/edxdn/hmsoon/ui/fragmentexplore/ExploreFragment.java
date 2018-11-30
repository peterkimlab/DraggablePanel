package com.edxdn.hmsoon.ui.fragmentexplore;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
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

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private ExploreFragmentAdapter mAdapter;
    private List<Datums> sentenceList, patternList, popularList, chatList;
    private HashMap<String, List<Datums>> dataset;
    private APIInterface apiInterface;

    private CompositeDisposable disposable = new CompositeDisposable();
    private static final String TAG = ExploreFragment.class.getSimpleName();

    public static ExploreFragment newInstance() {
        return new ExploreFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_explore, container, false);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        dataset = new HashMap<String, List<Datums>>();

        sentenceList = new ArrayList<>();
        patternList = new ArrayList<>();
        popularList = new ArrayList<>();
        chatList = new ArrayList<>();

        // 아덥터
        mAdapter = new ExploreFragmentAdapter(getActivity(), dataset);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rv.setLayoutManager(layoutManager);
        binding.rv.setAdapter(mAdapter);

        getDataRetrofit();

        return binding.getRoot();
    }

    private void getDataRetrofit() {
        getDataSentence(1, "");
        getDataPattern(1, "");
        getDataPopularSentences(1, "");
        getDataChat(1, "");
    }

    // 추천문장
    public void getDataSentence(int current_page, String pattern) {
        disposable.add(
                apiInterface.getPatterns(current_page + "", "")
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SearchResource>() {
                            @Override
                            public void onNext(SearchResource searchResource) {
                                if (searchResource != null && searchResource.hits != null) {
                                    sentenceList.addAll(searchResource.hits.hits);
                                    dataset.put(DataTypeMusicFragment.EXPLORE_SENTENCE_TYPE, sentenceList);
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

    // 추천패턴
    public void getDataPattern(int current_page, String pattern) {
        disposable.add(
                apiInterface.getPatterns(current_page + "", "")
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SearchResource>() {
                            @Override
                            public void onNext(SearchResource searchResource) {
                                if (searchResource != null && searchResource.hits != null) {
                                    patternList.addAll(searchResource.hits.hits);
                                    dataset.put(DataTypeMusicFragment.EXPLORE_PATTERN_TYPE, patternList);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                Log.e(TAG, "onComplete: " + "getDataPattern");
                            }
                        })
        );
    }

    //인기영상
    public void getDataPopularSentences(int current_page, String sort) {
        disposable.add(
                apiInterface.getSentences(current_page + "", "", "","")
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SearchResource>() {
                            @Override
                            public void onNext(SearchResource searchResource) {
                                if (searchResource != null && searchResource.hits != null) {
                                    popularList.addAll(searchResource.hits.hits);
                                    dataset.put(DataTypeMusicFragment.EXPLORE_POPULAR_TYPE, popularList);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                Log.e(TAG, "onComplete: " + "getDataPopularSentences");
                            }
                        })
        );
    }

    //채팅
    public void getDataChat(int current_page, String interest) {
        disposable.add(
                apiInterface.getInterests(current_page + "", "")
                        .toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SearchResource>() {
                            @Override
                            public void onNext(SearchResource searchResource) {
                                if (searchResource != null && searchResource.hits != null) {
                                    chatList.addAll(searchResource.hits.hits);
                                    dataset.put(DataTypeMusicFragment.EXPLORE_CHAT_TYPE, chatList);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                Log.e(TAG, "onComplete: " + "getDataChat");
                            }
                        })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //disposable.dispose();
    }
}