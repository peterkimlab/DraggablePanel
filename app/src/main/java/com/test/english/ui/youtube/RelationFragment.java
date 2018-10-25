package com.test.english.ui.youtube;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exam.english.R;
import com.test.english.api.APIClient;
import com.test.english.api.APIInterface;
import com.test.english.api.Datums;
import com.test.english.api.SearchResource;
import com.test.english.ui.adapter.EndlessRecyclerOnScrollListener;
import com.test.english.ui.adapter.RecyclerItemClickListener;
import com.test.english.ui.adapter.SpacesItemDecoration;
import com.test.english.ui.main.MainActivity;
import com.test.english.util.HummingUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class RelationFragment extends Fragment implements MainActivity.onKeyBackPressedListener {
    private String Tag = "RelationFragment";

    private EmptyRecyclerView playlisRecyclerView;
    private TextView emptyList;
    private APIInterface apiInterface;

    private List<Datums> datumList;
    private List<Datums> playList;
    private RelationAdapter mAdapter;
    private Handler mHandler;

    boolean autoPlay = false;

    private MainActivity mainActivity;
    EndlessRecyclerOnScrollListener es;

    public RelationFragment() {
    }

    public static RelationFragment newInstance() {
        return new RelationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            searchSentence = getArguments().getString(ARG_PARAM1);
            Log.e("test",searchSentence);
        }*/

        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_relation, container, false);

        datumList = new ArrayList<>();
        playList = new ArrayList<>();

        playlisRecyclerView = (EmptyRecyclerView)view.findViewById(R.id.your_play_list);
        emptyList = (TextView)view.findViewById(R.id.emptyList);

        SpacesItemDecoration decoration = new SpacesItemDecoration(5);
        playlisRecyclerView.addItemDecoration(decoration);
        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), 2);
        gridLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position == 0){
                    return 2;
                }else{
                    return 1;
                }
            }
        });
        playlisRecyclerView.setLayoutManager(gridLayout);
        playlisRecyclerView.setEmptyView(emptyList);
       /* DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                playlisRecyclerView.getContext(),  LinearLayoutManager.VERTICAL
        );
        playlisRecyclerView.addItemDecoration(mDividerItemDecoration);*/
       /* LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        playlisRecyclerView.setLayoutManager(layoutManager);*/
        es = new EndlessRecyclerOnScrollListener(gridLayout) {
            @Override
            public void onLoadMore(int current_page) {
                getData(current_page+1, StringReplace(MainActivity.SEARCH_POPUP_VALUE.startsWith("-") ? MainActivity.SEARCH_POPUP_VALUE.substring(1) : MainActivity.SEARCH_POPUP_VALUE));
            }

            @Override
            public void onScrolledToTop(int current_page) {
                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override public void run() {
                        //playlisRecyclerView.smoothScrollBy(0, 100);
                        mainActivity.getVideoListFragment().hideTab();

                    }
                }, 1000);
            }

            @Override
            public void onScrolledUpDown(int dy) {
                // Log.d("test","current_page================================================1");
                if(dy == 1){
                   // mainActivity.getVideoListFragment().showPanel();
                }else if(dy == 2){
                   // mainActivity.getVideoListFragment().hidePanel();
                }

            }

        };
        playlisRecyclerView.addOnScrollListener(es);

        playlisRecyclerView.setHasFixedSize(true);
        playlisRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), playlisRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        mAdapter.notifyItemChanged(mAdapter.getSeletedPosition());
                        mAdapter.setSeletedPosition(position);
                        mAdapter.notifyItemChanged(position);

                        Message message= Message.obtain();
                        message.what = position;
                        mHandler.sendMessage(message);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                    }
                })
        );


        mainActivity = (MainActivity) getActivity();

        mHandler = new Handler(){
            public void handleMessage(Message msg){
                Datums datums = datumList.get(msg.what);
                mainActivity.setVideoEpisodeUrl(datums, msg.what, false);
            }
        };

        mAdapter = new RelationAdapter(getActivity(), datumList);
        playlisRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public static String StringReplace(String str){
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        str =str.replaceAll(match, " ");
        return str;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MainActivity.SEARCH_CHECK){
            datumList.clear();
            getData(1, StringReplace(MainActivity.SEARCH_POPUP_VALUE.startsWith("-") ? MainActivity.SEARCH_POPUP_VALUE.substring(1) : MainActivity.SEARCH_POPUP_VALUE));
        }
     }

    public void getData(int current_page, String sentence) {
        if(datumList.size() == 0){
            datumList.add(mainActivity.getBaseSentence());
        }

        sentence = sentence.replaceAll("'","");

        Call<SearchResource> call = apiInterface.getRelation(current_page+"", sentence, MainActivity.SEARCH_IDS_VALUE);
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if( autoPlay){
                    autoPlay = false;
                    if(playList.size() == 0){
                        playList.addAll(datumList);
                    }
                    playList.addAll(resource.hits.hits);
                    /*Datums datums = playList.get(nowPosition);
                    mainActivity.setVideoEpisodeUrl(datums);*/
                }else{
                    boolean check = false;
                    if(datumList.size() == 0){
                        check = true;
                    }
                    if(resource != null && resource.hits != null){
                        Log.e("test","=====================aaaaa");
                        List aaa = new ArrayList();
                        for (int i = 0; i < resource.hits.hits.size(); i++) {
                            Log.e("test","============"+resource.hits.hits.get(i).source.get(HummingUtils.ElasticField.TEXT_EN).toString().trim()+"=========cccc");
                            if(resource.hits.hits.get(i).source.get(HummingUtils.ElasticField.TEXT_EN).toString().trim().equals("I'm out of here.")){
                                Log.e("test","=====================bbbbb");
                                datumList.add(resource.hits.hits.get(i));
                            }
                            if(resource.hits.hits.get(i).source.get(HummingUtils.ElasticField.TEXT_EN).toString().trim().equals("I'm out of here!")){
                                Log.e("test","=====================bbbbb2");
                                datumList.add(resource.hits.hits.get(i));
                            }
                        }
                        datumList.addAll(resource.hits.hits);
                    }
                   if(check){
                       playlisRecyclerView.scrollToPosition(0);
                       mAdapter.setSeletedPosition(1);
                   }

                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                Log.e(Tag,"Error : "+t.getMessage());
                call.cancel();
            }
        });
    }

    public List<Datums> getPlayList(int nowPosition){
        nowPosition++;
        if(datumList.size()-1 < nowPosition && datumList.size() < 20){
            autoPlay = true;
            es.actLoadMore();
        }
        return datumList;
    }

    @Override
    public void onBack() {
        mainActivity.setOnKeyBackPressedListener(null);
        mainActivity.onBackPressed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) getActivity()).setOnKeyBackPressedListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
