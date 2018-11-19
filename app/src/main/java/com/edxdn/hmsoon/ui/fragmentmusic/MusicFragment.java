package com.edxdn.hmsoon.ui.fragmentmusic;

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
import com.edxdn.hmsoon.application.MyCustomApplication;
import com.edxdn.hmsoon.util.HummingUtils;
import com.exam.english.R;
import com.exam.english.databinding.FragmentExploreBinding;
import com.edxdn.hmsoon.api.APIClient;
import com.edxdn.hmsoon.api.APIInterface;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.api.SearchResource;
import com.edxdn.hmsoon.ui.data.DataTypeMusicFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicFragment extends Fragment {

    private FragmentExploreBinding binding;
    private MusicFragmentAdapter mAdapter;
    private List<Datums> rankingList, recentList, motherGooseList, recommendList;
    private HashMap<String, List<Datums>> dataset;
    private APIInterface apiInterface;

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
        Handler getDataSentenceHandler = new Handler();
        getDataSentenceHandler.postDelayed(new Runnable() {
            @Override public void run() {
                getDataRanking(1, "");
            }
        }, 0);

        Handler getDataPatternHandler = new Handler();
        getDataPatternHandler.postDelayed(new Runnable() {
            @Override public void run() {
                getDataRecent(1, "");
            }
        }, 100);

        Handler getDataMotherGooseHandler = new Handler();
        getDataMotherGooseHandler.postDelayed(new Runnable() {
            @Override public void run() {
                getDataMotherGoose(1, "");
            }
        }, 500);

        Handler getDataChatHandler = new Handler();
        getDataChatHandler.postDelayed(new Runnable() {
            @Override public void run() {
                getDataChat(1, "");
            }
        }, 1000);

    }

    // 인기순위
    public void getDataRanking(int current_page, String sort) {
        Call<SearchResource> call = apiInterface.getPopular(current_page+"", "", sort);
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if (resource != null && resource.hits != null) {
                    rankingList.addAll(resource.hits.hits);
                }
                dataset.put(DataTypeMusicFragment.MUSIC_RANKING_TYPE, rankingList);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

    //최근 음악
    public void getDataRecent(int current_page, String sort) {
        Call<SearchResource> call = apiInterface.getContents(current_page+"", "youtube_mothergoose");
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if (resource != null && resource.hits != null) {
                    recentList.addAll(resource.hits.hits);
                }
                dataset.put(DataTypeMusicFragment.MUSIC_RECENT_TYPE, recentList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

    //추천 동요
    public void getDataMotherGoose(int current_page, String sort) {
        Call<SearchResource> call = apiInterface.getContents(current_page+"", "youtube_mothergoose");
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if (resource != null && resource.hits != null) {
                    motherGooseList.addAll(resource.hits.hits);
                }
                dataset.put(DataTypeMusicFragment.MUSIC_MOTHERGOOSE_TYPE, motherGooseList);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

    //추천 음악
    public void getDataChat(int current_page, String interest) {
        Call<SearchResource> call = apiInterface.getPopular(current_page+"", "", interest);
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if (resource != null && resource.hits != null) {
                    recommendList.addAll(resource.hits.hits);
                }
                dataset.put(DataTypeMusicFragment.MUSIC_RECOMMEND_TYPE, recommendList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }
}