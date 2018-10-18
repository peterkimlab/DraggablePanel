package com.test.english.ui.fragmentmusic;

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
import com.test.english.api.APIClient;
import com.test.english.api.APIInterface;
import com.test.english.api.Datums;
import com.test.english.api.SearchResource;
import com.test.english.ui.data.DataTypeMusicFragment;
import com.test.english.ui.adapter.MusicFragmentAdapter;
import com.test.english.ui.adapter.SpacesItemDecoration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicFragment extends Fragment {

    private FragmentMusicBinding binding;
    private MusicFragmentAdapter mAdapter;
    private List<Datums> sentenceList, patternList;
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
        sentenceList = new ArrayList<>();
        patternList = new ArrayList<>();
        dataset = new HashMap<String, List<Datums>>();

        SpacesItemDecoration decoration = new SpacesItemDecoration(5);

        // 최신문장 데이터
        Handler handler0 = new Handler();
        handler0.postDelayed(new Runnable() {
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

        // 최신문장 레이이웃
        //binding.display03.addItemDecoration(decoration);

        // 아덥터
        mAdapter = new MusicFragmentAdapter(getActivity(), dataset);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rv.setLayoutManager(linearLayoutManager);
        //binding.display03.setHasFixedSize(true);

        binding.rv.setAdapter(mAdapter);
        //binding.display03.setNestedScrollingEnabled(false);

        /*binding.rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), binding.display03 ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Datums datums = dataList.get(position);
                        //mainActivity.setVideoUrl(datums);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );*/

        return binding.getRoot();
    }

    // 최신문장 레트로핏
    public void getDataLatestSentences(int current_page, String sort) {
        Call<SearchResource> call = apiInterface.getSentences(current_page+"", "", sort, "");
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if(resource != null && resource.hits != null) {
                    sentenceList.addAll(resource.hits.hits);
                }
                dataset.put(DataTypeMusicFragment.SENTENCE_TYPE, sentenceList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                //Log.e(TAG,t.getMessage());
                call.cancel();
            }
        });
    }

    public void getDataDiscover(int current_page) {
        Call<List<SearchResource>> call = apiInterface.getDiscover(current_page+"", "");
        call.enqueue(new Callback<List<SearchResource>>() {
            @Override
            public void onResponse(Call<List<SearchResource>> call, Response<List<SearchResource>> response) {
                List<SearchResource> resource = response.body();
                if (resource != null) {
                    for (int i = 0; i < resource.size(); i++) {
                        if (resource.get(i).typeName.equals("pattern") && resource.get(i).hits.hits != null) {
                            patternList.addAll(resource.get(i).hits.hits);
                            dataset.put(DataTypeMusicFragment.PATTERN_TYPE, patternList);
                        } else if (resource.get(i).typeName.equals("word") && resource.get(i).hits.hits != null) {
                            //datumsList.addAll(resource.get(i).hits.hits);
                            //dataset.add(datumsList);
                        } else if (resource.get(i).typeName.equals("genre") && resource.get(i).hits.hits != null) {
                            //datumsList.addAll(resource.get(i).hits.hits);
                            //dataset.add(datumsList);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<SearchResource>> call, Throwable t) {
                call.cancel();
            }
        });
    }
}
