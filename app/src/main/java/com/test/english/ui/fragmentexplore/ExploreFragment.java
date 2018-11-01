package com.test.english.ui.fragmentexplore;

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
import com.test.english.api.APIClient;
import com.test.english.api.APIInterface;
import com.test.english.api.Datums;
import com.test.english.api.SearchResource;
import com.test.english.application.MyCustomApplication;
import com.test.english.ui.data.DataTypeMusicFragment;
import com.test.english.util.HummingUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private ExploreFragmentAdapter mAdapter;
    private List<Datums> sentenceList, patternList;
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

        // 아덥터
        mAdapter = new ExploreFragmentAdapter(getActivity(), dataset);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rv.setLayoutManager(layoutManager);
        binding.rv.setAdapter(mAdapter);

        /*
        binding.rv.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getDataPattern(current_page + 1, "");
            }

            @Override
            public void onScrolledToTop(int current_page) {
            }

            @Override
            public void onScrolledUpDown(int dy) {
            }
        });
        binding.rv.setHasFixedSize(true);
        binding.rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), binding.rv ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Message message= Message.obtain();
                        message.what = position;
                        mHandler.sendMessage(message);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );*/

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
        Call<SearchResource> call = apiInterface.getPatterns(current_page+"", pattern);
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

}