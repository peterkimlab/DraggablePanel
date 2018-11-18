package com.edxdn.hmsoon.ui.fragmentexplore;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.exam.english.R;
import com.exam.english.databinding.FragmentExploreBinding;
import com.edxdn.hmsoon.api.APIClient;
import com.edxdn.hmsoon.api.APIInterface;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.api.SearchResource;
import com.edxdn.hmsoon.application.MyCustomApplication;
import com.edxdn.hmsoon.ui.data.DataTypeMusicFragment;
import com.edxdn.hmsoon.util.HummingUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private ExploreFragmentAdapter mAdapter;
    private List<Datums> sentenceList, patternList, popularList, chatList;
    private HashMap<String, List<Datums>> dataset;
    private APIInterface apiInterface;
    private Handler mHandler;

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

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                Datums datums = patternList.get(msg.what);
                MyCustomApplication.getMainInstance().onClickItems("sentences", datums.source.get(HummingUtils.ElasticField.PATTERN).toString());
            }
        };

        getDataRetrofit();

        return binding.getRoot();
    }

    private void getDataRetrofit() {
        Handler getDataSentenceHandler = new Handler();
        getDataSentenceHandler.postDelayed(new Runnable() {
            @Override public void run() {
                getDataSentence(1, "");
            }
        }, 0);

        Handler getDataPatternHandler = new Handler();
        getDataPatternHandler.postDelayed(new Runnable() {
            @Override public void run() {
                getDataPattern(1, "");
            }
        }, 100);

        Handler getDataPopularSentencesHandler = new Handler();
        getDataPopularSentencesHandler.postDelayed(new Runnable() {
            @Override public void run() {
                getDataPopularSentences(1, "");
            }
        }, 500);

        Handler getDataChatHandler = new Handler();
        getDataChatHandler.postDelayed(new Runnable() {
            @Override public void run() {
                getDataChat(1, "");
            }
        }, 1000);

    }

    // 추천문장
    public void getDataSentence(int current_page, String pattern) {
        Call<SearchResource> call = apiInterface.getPatterns(current_page+"", pattern);
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if (resource != null && resource.hits != null) {
                    sentenceList.addAll(resource.hits.hits);
                }
                dataset.put(DataTypeMusicFragment.EXPLORE_SENTENCE_TYPE, sentenceList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

    // 추천패턴
    public void getDataPattern(int current_page, String pattern) {
        Call<SearchResource> call = apiInterface.getPatterns(current_page + "", pattern);
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if (resource != null && resource.hits != null) {
                    patternList.addAll(resource.hits.hits);
                }
                dataset.put(DataTypeMusicFragment.EXPLORE_PATTERN_TYPE, patternList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

    //인기영상
    public void getDataPopularSentences(int current_page, String sort) {
        //Call<SearchResource> call = apiInterface.getPopular(current_page+"", "", sort);
        Call<SearchResource> call = apiInterface.getSentences(current_page + "", "", sort, "");
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if (resource != null && resource.hits != null) {
                    popularList.addAll(resource.hits.hits);
                }
                dataset.put(DataTypeMusicFragment.EXPLORE_POPULAR_TYPE, popularList);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

    //채팅
    public void getDataChat(int current_page, String interest) {
        Call<SearchResource> call = apiInterface.getInterests(current_page + "", "");
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if(resource != null && resource.hits != null){
                    chatList.addAll(resource.hits.hits);
                }
                dataset.put(DataTypeMusicFragment.EXPLORE_CHAT_TYPE, chatList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

}