package com.edxdn.hmsoon.ui.fragmentmusic;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.exam.english.R;
import com.exam.english.databinding.FragmentMusicBinding;
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

    private FragmentMusicBinding binding;
    private MusicFragmentAdapter mAdapter;
    private List<Datums> popularList, motherGooseList, latestList, patternList;
    private HashMap<String, List<Datums>> dataset;
    private APIInterface apiInterface;

    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_music, container, false);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        dataset = new HashMap<String, List<Datums>>();

        popularList = new ArrayList<>();
        motherGooseList = new ArrayList<>();
        latestList = new ArrayList<>();
        patternList = new ArrayList<>();

        //SpacesItemDecoration decoration = new SpacesItemDecoration(5);
        //binding.display03.addItemDecoration(decoration);

        getDataByRetrofit();

        // 아덥터
        mAdapter = new MusicFragmentAdapter(getActivity(), dataset);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rv.setLayoutManager(linearLayoutManager);
        binding.rv.setAdapter(mAdapter);

        return binding.getRoot();
    }

    private void getDataByRetrofit() {

        // 인기문장 데이터
        Handler getDataPopularSetenceHandler = new Handler();
        getDataPopularSetenceHandler.postDelayed(new Runnable() {
            @Override public void run() {
                getDataPopularSentences(1, "viewcnt");
            }
        }, 0);

        // 동요 데이터
        Handler getDataMotherGooseSetenceHandler = new Handler();
        getDataMotherGooseSetenceHandler.postDelayed(new Runnable() {
            @Override public void run() {
                getDataMotherGooseSentences(1);
            }
        }, 100);

        // 최신문장 데이터
        Handler getDataLatestSetenceHandler = new Handler();
        getDataLatestSetenceHandler.postDelayed(new Runnable() {
            @Override public void run() {
                getDataLatestSentences(1, "");
            }
        }, 500);

        /*Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override public void run() {
                getDataDiscover(1);
            }
        }, 1000);*/
    }

    // 인기문장 레트로핏
    public void getDataPopularSentences(int current_page, String sort) {
        Call<SearchResource> call = apiInterface.getPopular(current_page+"", "", sort);
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if (resource != null && resource.hits != null) {
                    popularList.addAll(resource.hits.hits);
                }
                dataset.put(DataTypeMusicFragment.POPULAR_TYPE, popularList);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

    // 동요 레트로핏
    public void getDataMotherGooseSentences(int current_page) {
        Call<SearchResource> call = apiInterface.getContents(current_page+"", "youtube_mothergoose");
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if (resource != null && resource.hits != null) {
                    motherGooseList.addAll(resource.hits.hits);
                }
                dataset.put(DataTypeMusicFragment.MOTHERGOOSE_TYPE, motherGooseList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

    // 최신문장 레트로핏
    public void getDataLatestSentences(int current_page, String sort) {
        Call<SearchResource> call = apiInterface.getSentences(current_page+"", "", sort, "");
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if (resource != null && resource.hits != null) {
                    latestList.addAll(resource.hits.hits);
                }
                dataset.put(DataTypeMusicFragment.SENTENCE_TYPE, latestList);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

}