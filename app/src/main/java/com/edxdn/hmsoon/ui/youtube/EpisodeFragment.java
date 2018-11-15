package com.edxdn.hmsoon.ui.youtube;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.exam.english.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.edxdn.hmsoon.api.APIClient;
import com.edxdn.hmsoon.api.APIInterface;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.api.SearchResource;
import com.edxdn.hmsoon.entities.YoutubeChannel;
import com.edxdn.hmsoon.ui.adapter.EndlessRecyclerOnScrollListener;
import com.edxdn.hmsoon.ui.adapter.RecyclerItemClickListener;
import com.edxdn.hmsoon.ui.data.DeveloperKey;
import com.edxdn.hmsoon.ui.main.MainActivity;
import com.edxdn.hmsoon.util.HummingUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class EpisodeFragment extends Fragment implements MainActivity.onKeyBackPressedListener {
    private String Tag = "EpisodeFragment";

    private RecyclerView playlisRecyclerView;
    private APIInterface apiInterface;
    private APIInterfaceYoutube apiInterfaceYoutube;
    private List<Datums> datumList;
    private List<Datums> playList;
    private EpisodeAdapter mAdapter;
    private Handler mHandler;
    boolean autoPlay = false;
    private MainActivity mainActivity;
    EndlessRecyclerOnScrollListener es;
    RecyclerItemClickListener.OnItemClickListener clickListener;
    EpisodeListener listener;

//    SwipeRefreshLayout mSwipeRefreshLayout;

    YoutubeChannel channelInfo;

    SwipeRefreshLayout mSwipeRefreshLayout;

    public EpisodeFragment() {
    }

    public static EpisodeFragment newInstance() {
        return new EpisodeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("test", Tag + "----B1");
        /*if (getArguments() != null) {
            searchSentence = getArguments().getString(ARG_PARAM1);
            Log.e("test",searchSentence);
        }*/

        apiInterface = APIClient.getClient().create(APIInterface.class);
        apiInterfaceYoutube = APIClientYoutube.getClient().create(APIInterfaceYoutube.class);
        listener = new EpisodeListener() {
            @Override
            public YoutubeChannel getChannelInfo() {
                return getChannel();
            }
            @Override
            public void refresh() {
                refreshList();
            }

            @Override
            public void pageYoutubeChannel() {
                youtubeChannel();
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_episode, container, false);

        datumList = new ArrayList<>();
        playList = new ArrayList<>();
        playlisRecyclerView = (RecyclerView) view.findViewById(R.id.your_play_list);

        //SpacesItemDecoration decoration = new SpacesItemDecoration(5);
        //playlisRecyclerView.addItemDecoration(decoration);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                playlisRecyclerView.getContext(), LinearLayoutManager.VERTICAL
        );
        mDividerItemDecoration.setDrawable(new ColorDrawable(getResources().getColor(R.color.grey)));
        playlisRecyclerView.addItemDecoration(mDividerItemDecoration);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //layoutManager.scrollToPosition(currPos);
        playlisRecyclerView.setLayoutManager(layoutManager);

        es = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getData(current_page, MainActivity.SEARCH_IDS_VALUE, true);
            }

            @Override
            public void onScrolledToTop(int current_page) {

            }

            @Override
            public void onScrolledUpDown(int dy) {
                if (dy == 1) {
                   // mainActivity.getVideoListFragment().showPanel();
                } else if (dy == 2) {
                   // mainActivity.getVideoListFragment().hidePanel();
                }
            }
        };
        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });


        playlisRecyclerView.setOnScrollListener(es);
        playlisRecyclerView.setHasFixedSize(true);
        clickListener = new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapter.notifyItemChanged(mAdapter.getSeletedPosition());
                mAdapter.setSeletedPosition(position);
                mAdapter.notifyItemChanged(position);

                Message message= Message.obtain();
                message.what = position;
                mHandler.sendMessage(message);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        };

        playlisRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (gestureDetector.onTouchEvent(e)) {
                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                    if (child != null) {
                        int p = rv.findViewHolderForLayoutPosition(rv.getChildLayoutPosition(child)).getAdapterPosition();
                        if (p == 0) {
                            return false;
                        } else {
                            clickListener.onItemClick(child, p);
                        }
                    }
                }
                return false;
            }

            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        mainActivity = (MainActivity) getActivity();

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                Datums datums = datumList.get(msg.what);
                mainActivity.setVideoEpisodeUrl(datums, msg.what, true);
            }
        };

        mAdapter = new EpisodeAdapter(getActivity(), datumList, listener);
        playlisRecyclerView.setAdapter(mAdapter);

       /* mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.SEARCH_CHECK = true;

                if(MainActivity.SEARCH_CHECK && !MainActivity.SEARCH_IDS_VALUE.equals("")){
                    datumList.clear();
                    String[] ids = MainActivity.SEARCH_IDS_VALUE.split("_");
                    int currentPage = (Integer.valueOf(ids[3])/10)+1;
                    es.setCurrent_page(currentPage);
                    getData(currentPage, MainActivity.SEARCH_IDS_VALUE, true);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                         mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);

            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );*/

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                getDataChannel();

               /* String url = "https://www.googleapis.com/youtube/v3/channels?part=snippet,statistics&id=UCz4tgANd4yy8Oe0iXCdSWfA&key=AIzaSyAhAECrmHzCWbZlg2P60l_2vOsxakLP5HQ\n";
                //Perform the doInBackground method, passing in our url
                try {
                    Log.e("test", "ssss"+APIClientYoutube.getClient().baseUrl().toString());
                    HttpGetRequest getRequest = new HttpGetRequest();
                    getRequest.execute(url).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }*/
            }
        }, 1000);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.SEARCH_CHECK = false;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int p = es.getMinCurrentPage()-1;
                        es.setMinCurrentPage(p);
                        getData(p, MainActivity.SEARCH_IDS_VALUE, false);
                    }
                }, 10);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 800);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.SEARCH_CHECK && !MainActivity.SEARCH_IDS_VALUE.equals("")) {
            datumList.clear();
            String[] ids = MainActivity.SEARCH_IDS_VALUE.split("_");
            int currentPage = (Integer.valueOf(ids[3]) / 10) + 1;
            es.setCurrent_page(currentPage);
            getData(currentPage, MainActivity.SEARCH_IDS_VALUE, true);
        }
    }

    public void getData(int current_page, String searchIds, final boolean order) {
        if(current_page > 0){
            Call<SearchResource> call = apiInterface.doGetEpisodeList(current_page + "", searchIds);
            call.enqueue(new Callback<SearchResource>() {
                @Override
                public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                    SearchResource resource = response.body();
                    if (resource != null && resource.hits != null) {
                        if (datumList.size() == 0) {
                            datumList.add(new Datums());
                        }
                        if (autoPlay) {
                            autoPlay = false;
                            if (playList.size() == 0) {
                                playList.addAll(datumList);
                            }
                            playList.addAll(resource.hits.hits);
                    /*Datums datums = playList.get(nowPosition);
                    mainActivity.setVideoEpisodeUrl(datums);*/
                        } else {

                            if (resource != null && resource.hits != null) {
                                if (order) {
                                    datumList.addAll(resource.hits.hits);
                                } else {
                                    List<Datums> list = resource.hits.hits;
                                    Collections.sort(list, new CompareNcodeDesc());

                                    for (int i = 0; i < list.size(); i++) {
                                        datumList.add(i + 1, list.get(i));
                                    }
                                }

                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                }
                @Override
                public void onFailure(Call<SearchResource> call, Throwable t) {
                    Log.d("test", "================================================2" + t.getMessage());
                    call.cancel();
                }
            });
        }
    }

    //https://www.googleapis.com/youtube/v3/channels?part=snippet,statistics&id=UCz4tgANd4yy8Oe0iXCdSWfA&key=

    public void getDataChannel() {
        String part = "snippet,statistics";
        String id = MainActivity.SEARCH_YOUTUBE_CHANNEL_VALUE;
        String key = DeveloperKey.DEVELOPER_KEY;
        if(id != null && !id.equals("")){
            Call<YoutubeChannel> call = apiInterfaceYoutube.getYoutubeChannelInfo(part, id, key);
            call.enqueue(new Callback<YoutubeChannel>() {
                @Override
                public void onResponse(Call<YoutubeChannel> call, Response<YoutubeChannel> response) {
                    channelInfo = response.body();
                    mAdapter.notifyItemChanged(0);
                }
                @Override
                public void onFailure(Call<YoutubeChannel> call, Throwable t) {
                    Log.d("test", "================================================2" + t.getMessage());
                    call.cancel();
                }
            });
        }
    }

    public List<Datums> getPlayList(int nowPosition) {
        nowPosition++;
        if (datumList.size() - 1 < nowPosition && datumList.size() < 20) {
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

    public void refreshList() {
        Log.e("test", "+++++++++++++++++++++++++++++++++++");
        if(!MainActivity.SEARCH_IDS_VALUE.equals("")){
            Log.e("test", "+++++++++++++++++++++++++++++++++++2");
            datumList.clear();
            String[] ids = MainActivity.SEARCH_IDS_VALUE.split("_");
            int currentPage = (Integer.valueOf(ids[3])/10)+1;
            es.setCurrent_page(currentPage);
            getData(currentPage, MainActivity.SEARCH_IDS_VALUE, true);
        }
    }

    public YoutubeChannel getChannel() {
        return channelInfo;
    }

    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }
    public void youtubeChannel() {
        watchYoutubeVideo(getContext(), mainActivity.SEARCH_YOUTUBE_VALUE);
    }


    static class CompareNcodeDesc implements Comparator<Datums> {
        @Override
        public int compare(Datums o1, Datums o2) {
            int a = Integer.valueOf(o2.source.get(HummingUtils.ElasticField.NCODE).toString());
            int b = Integer.valueOf(o1.source.get(HummingUtils.ElasticField.NCODE).toString());
            return a > b ? -1 : a < b ? 1 : 0;
        }
    }

    public interface EpisodeListener{
        YoutubeChannel getChannelInfo();
        void refresh();
        void pageYoutubeChannel();
    }

    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 5000;
        public static final int CONNECTION_TIMEOUT = 5000;
        @Override
        protected String doInBackground(String... params){
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.e("test", "----------"+result);
            Gson gson = new Gson();
            Type type = new TypeToken<YoutubeChannel>(){}.getType();
            channelInfo = gson.fromJson(result, type);
            //channelInfo = gson.fromJson(result, YoutubeChannel.class);
            mAdapter.notifyItemChanged(0);
        }
    }
}
