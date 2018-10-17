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
import com.test.english.ui.adapter.MainItemDisplay03Adapter;
import com.test.english.ui.adapter.RecyclerItemClickListener;
import com.test.english.ui.adapter.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicFragment extends Fragment {

    private FragmentMusicBinding binding;
    private MainItemDisplay03Adapter mAdapter03;
    private List<Datums> dataList03;
    private APIInterface apiInterface;

    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_music, container, false);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        dataList03 = new ArrayList<>();

        Handler handler0 = new Handler();
        handler0.postDelayed(new Runnable() {
            @Override public void run() {
                getDataLatestSentences(1, "");
            }
        }, 500);


        SpacesItemDecoration decoration = new SpacesItemDecoration(5);

        binding.display03.addItemDecoration(decoration);
        LinearLayoutManager layoutManager03 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.display03.setLayoutManager(layoutManager03);
        binding.display03.setHasFixedSize(true);

        mAdapter03 = new MainItemDisplay03Adapter(getActivity(), dataList03);
        binding.display03.setAdapter(mAdapter03);
        binding.display03.setNestedScrollingEnabled(false)
        ;
        binding.display03.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), binding.display03 ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Datums datums = dataList03.get(position);
                        //mainActivity.setVideoUrl(datums);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );

        return binding.getRoot();
    }

    public void getDataLatestSentences(int current_page, String sort) {
        Call<SearchResource> call = apiInterface.getSentences(current_page+"", "", sort, "");
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if(resource != null && resource.hits != null){
                    dataList03.addAll(resource.hits.hits);
                }
                mAdapter03.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                //Log.e(TAG,t.getMessage());
                call.cancel();
            }
        });
    }
}
