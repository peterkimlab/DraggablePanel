package com.edxdn.hmsoon.ui.youtube;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edxdn.hmsoon.R;
import com.edxdn.hmsoon.api.APIClient;
import com.edxdn.hmsoon.api.APIInterface;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.api.SearchResource;
import com.edxdn.hmsoon.application.MyCustomApplication;
import com.edxdn.hmsoon.ui.adapter.EndlessRecyclerOnScrollListener;
import com.edxdn.hmsoon.ui.adapter.ProgressBar;
import com.edxdn.hmsoon.ui.adapter.RecyclerItemClickListener;
import com.edxdn.hmsoon.ui.adapter.SpacesItemDecoration;
import com.edxdn.hmsoon.ui.main.MainActivity;
import com.edxdn.hmsoon.util.HummingUtils;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class PlayMusicFragment extends Fragment implements MainActivity.onKeyBackPressedListener {

    private YouTubePlayerView youTubePlayerView;
    private com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer youTubePlayer;
    private String currentlySelectedId;
    private String yid;
    private String ids;

    private RecyclerView playlisRecyclerView;
    private List<Datums> datumList;
    private PlayMusicAdapter mAdapter;
    EndlessRecyclerOnScrollListener es;

    private APIInterface apiInterface;
    private ProgressBar songProgress;
    boolean runProgress = true;
    boolean runPositionProgress = true;
    private static Handler songProgressHandler;
    //PlayerMusicActivity playerMusicActivity;
    LinearLayout textlayout;
    FrameLayout bottom_layout, bottomBarFrameLayout;

    LinearLayoutManager layoutManager;

    TextView speakko;
    TextView alltextkr;
    TextView alltext;

    //private Button text, textt, texts;
    private TextView deepdive, youtube;
    private ImageView repeat, rewind, forward;
    private ImageView play_pause_toggle, small_video_down;

    int nowPosition = 0;
    boolean repeatFlg = false;
    int repeatPosition = 0;

    private boolean playing = true;

    View view;
    public PlayMusicFragment() {
    }

    public static PlayMusicFragment newInstance() {
        return new PlayMusicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_playmusic, container, false);

        datumList = new ArrayList<>();

        youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        initYouTubePlayerView();

        SpacesItemDecoration decoration = new SpacesItemDecoration(5);

        playlisRecyclerView = (RecyclerView) view.findViewById(R.id.your_play_list);

        playlisRecyclerView.addItemDecoration(decoration);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                playlisRecyclerView.getContext(), LinearLayoutManager.VERTICAL
        );
        playlisRecyclerView.addItemDecoration(mDividerItemDecoration);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //layoutManager.scrollToPosition(currPos);
        playlisRecyclerView.setLayoutManager(layoutManager);

        es = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                getData(current_page);
            }

            @Override
            public void onScrolledToTop(int current_page) {
                getData(current_page);
            }

            @Override
            public void onScrolledUpDown(int dy) {
                if (dy != 3) {
                    runPositionProgress = false;
                } else {
                    Handler handler0 = new Handler();
                    handler0.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runPositionProgress = true;
                        }
                    }, 1000);

                }
            }
        };
        playlisRecyclerView.setOnScrollListener(es);
        playlisRecyclerView.setHasFixedSize(true);
        playlisRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), playlisRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        mAdapter.notifyItemChanged(mAdapter.getSeletedPosition());
                        mAdapter.setSeletedPosition(position);
                        mAdapter.notifyItemChanged(position);

                        Datums datums = datumList.get(position);
                        Log.e("test", "+++" + datums.source.get(HummingUtils.ElasticField.STIME).toString());
                        BigDecimal a = new BigDecimal(datums.source.get(HummingUtils.ElasticField.STIME).toString());
                        youTubePlayer.seekTo(a.floatValue());

                        //
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );

        mAdapter = new PlayMusicAdapter(getContext(), datumList);
        playlisRecyclerView.setAdapter(mAdapter);

        songProgress = (ProgressBar) view.findViewById(R.id.songProgress);
        songProgress.setMax(100);
        songProgress.setProgressWithAnim(0);

        speakko = view.findViewById(R.id.speakko);
        alltextkr = view.findViewById(R.id.alltextkr);
        alltext = view.findViewById(R.id.alltext);
        //text = view.findViewById(R.id.text);
        //textt = view.findViewById(R.id.textt);
        //texts = view.findViewById(R.id.texts);
        deepdive = view.findViewById(R.id.deepdive);
        youtube = view.findViewById(R.id.youtube);
        repeat = view.findViewById(R.id.repeat);
        rewind = view.findViewById(R.id.rewind);
        forward = view.findViewById(R.id.forward);
        play_pause_toggle = view.findViewById(R.id.play_pause_toggle);
        small_video_down = view.findViewById(R.id.small_video_down);
        textlayout = view.findViewById(R.id.textlayout);
        bottom_layout = view.findViewById(R.id.bottom_layout);
        bottomBarFrameLayout = view.findViewById(R.id.bottomBarFrameLayout);

        /*text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alltext.getVisibility() != View.VISIBLE) {
                    alltext.setVisibility(View.VISIBLE);
                } else {
                    alltext.setVisibility(View.GONE);
                }
            }
        });

        textt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alltextkr.getVisibility() != View.VISIBLE) {
                    alltextkr.setVisibility(View.VISIBLE);
                } else {
                    alltextkr.setVisibility(View.GONE);
                }
            }
        });*/

        /*texts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speakko.getVisibility() != View.VISIBLE) {
                    speakko.setVisibility(View.VISIBLE);
                } else {
                    speakko.setVisibility(View.GONE);
                }
            }
        });*/

        deepdive.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {

                //youTubePlayer.pause();
                Datums datums = datumList.get(nowPosition);
                MyCustomApplication.getMainInstance().setVideoUrl(datums);



            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                //youTubePlayer.pause();
                watchYoutubeVideo(getActivity(), MyCustomApplication.getMainInstance().SEARCH_YOUTUBE_VALUE);
            }
        });

        repeat.setTag("off");
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeat.getTag().toString().equals("off")){
                    repeat.setTag("one");
                    repeat.setImageResource(R.drawable.rep_all);
                    repeatFlg = true;
                    repeatPosition = nowPosition;
                }else if(repeat.getTag().toString().equals("one")){
                    repeat.setImageResource(R.drawable.rep_no);
                    repeat.setTag("off");
                    repeatFlg = false;
                }
            }
        });

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nowPosition-1 >= 0){
                    nowPosition--;
                    Datums datums = datumList.get(nowPosition);
                    BigDecimal a = new BigDecimal(datums.source.get(HummingUtils.ElasticField.STIME).toString());
                    youTubePlayer.seekTo(a.floatValue());
                }
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nowPosition+1 < datumList.size()){
                    nowPosition++;
                    Datums datums = datumList.get(nowPosition);
                    BigDecimal a = new BigDecimal(datums.source.get(HummingUtils.ElasticField.STIME).toString());
                    youTubePlayer.seekTo(a.floatValue());
                }
            }
        });

        play_pause_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playing){
                    youTubePlayer.pause();
                    play_pause_toggle.setImageResource(R.drawable.player_play);
                    playing = false;
                }else{
                    youTubePlayer.play();
                    play_pause_toggle.setImageResource(R.drawable.ic_action_pause);
                    playing = true;
                }
            }
        });

        small_video_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textlayout.getVisibility() != View.VISIBLE) {
                    textlayout.setVisibility(View.VISIBLE);
                    bottom_layout.setVisibility(View.VISIBLE);
                    small_video_down.setImageResource(R.drawable.up);

                    FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) bottomBarFrameLayout.getLayoutParams();
                    param.gravity = Gravity.BOTTOM;
                    bottomBarFrameLayout.setLayoutParams(param);

                } else {
                    textlayout.setVisibility(View.GONE);
                    bottom_layout.setVisibility(View.GONE);
                    small_video_down.setImageResource(R.drawable.down);

                    FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) bottomBarFrameLayout.getLayoutParams();
                    param.gravity = Gravity.TOP;
                    bottomBarFrameLayout.setLayoutParams(param);
                }
            }
        });

        Handler handler0 = new Handler();
        handler0.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData(1);
            }
        }, 0);
        return view;
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

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getData(int current_page) {
        Call<SearchResource> call = apiInterface.doGetEpisodeList(current_page + "", MyCustomApplication.getMainInstance().SEARCH_IDS_VALUE);
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                if (resource != null && resource.hits != null) {
                    datumList.addAll(resource.hits.hits);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                Log.e("test", t.getMessage());
                call.cancel();
            }
        });
    }

    private void initYouTubePlayerView() {
        initPlayerMenu();

        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.initialize(youTubePlayer2 -> {
            this.youTubePlayer = youTubePlayer2;

            youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady() {

                    String youtubeId = MyCustomApplication.getMainInstance().SEARCH_YOUTUBE_VALUE;
                    float startTime = 0;
                    String[] y = youtubeId.split("&t=");
                    if(y.length > 1){
                        startTime = new BigDecimal(y[1].replace("s","")).floatValue();
                    }

                    loadVideo(youTubePlayer, y[0], startTime);
                }
            });

            youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady() {
                    super.onReady();
                }

                @Override
                public void onStateChange(int state) {
                    /*1 –시작되지 않음
                    0 – 종료
                    1 – 재생 중
                    2 – 일시중지
                    3 – 버퍼링
                    5 – 동영상 신호*/
                    switch (state){
                        case -1 :
                            break;
                        case 0 :
                            playing = false;
                            play_pause_toggle.setImageResource(R.drawable.player_play);
                            break;
                        case 1 :
                            playing = true;
                            play_pause_toggle.setImageResource(R.drawable.ic_action_pause);
                            break;
                        case 2 :
                            playing = false;
                            play_pause_toggle.setImageResource(R.drawable.player_play);
                            break;
                        case 3 :
                            break;
                        case 5 :
                            break;
                    }
                    super.onStateChange(state);
                }

                @Override
                public void onPlaybackQualityChange(@NonNull String playbackQuality) {
                    super.onPlaybackQualityChange(playbackQuality);
                }

                @Override
                public void onPlaybackRateChange(@NonNull String rate) {
                    super.onPlaybackRateChange(rate);
                }

                @Override
                public void onError(int error) {
                    super.onError(error);
                }

                @Override
                public void onApiChange() {
                    super.onApiChange();
                }

                @Override
                public void onCurrentSecond(float second) {
                    updateProgress(new BigDecimal(second).doubleValue());
                    super.onCurrentSecond(second);
                }

                @Override
                public void onVideoDuration(float duration) {

                    maxProgress(new BigDecimal(duration).setScale(0, BigDecimal.ROUND_CEILING).intValue());
                    super.onVideoDuration(duration);
                }

                @Override
                public void onVideoLoadedFraction(float fraction) {
                    super.onVideoLoadedFraction(fraction);
                }

                @Override
                public void onVideoId(@NonNull String videoId) {
                    super.onVideoId(videoId);
                }
            });
        }, true);
    }

    public void updateProgress(double pos) {
        songProgress.setProgressWithAnim(new BigDecimal(pos).setScale(0, BigDecimal.ROUND_CEILING).intValue());

        Handler handlers2 = new Handler();
        handlers2.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (repeatFlg) {
                    Datums datums = datumList.get(repeatPosition);
                    BigDecimal s = new BigDecimal(datums.source.get(HummingUtils.ElasticField.STIME).toString());
                    BigDecimal e = new BigDecimal(datums.source.get(HummingUtils.ElasticField.ETIME).toString());

                    if(pos > e.intValue()){
                        youTubePlayer.seekTo(s.intValue());
                    }

                } else {
                    int startIdx = nowPosition;
                    for (int i = startIdx; i < datumList.size(); i++) {
                        Datums datums = datumList.get(i);
                        BigDecimal s = new BigDecimal(datums.source.get(HummingUtils.ElasticField.STIME).toString());
                        BigDecimal e = new BigDecimal(datums.source.get(HummingUtils.ElasticField.ETIME).toString());
                        if (pos >= s.doubleValue() && pos <= e.doubleValue()) {
                            int idx = i;
                            nowPosition = idx;
                            if (mAdapter.getSeletedPosition() != idx || idx ==0) {
                                mAdapter.notifyItemChanged(mAdapter.getSeletedPosition());
                                mAdapter.setSeletedPosition(idx);
                                mAdapter.notifyItemChanged(idx);
                                Datums datumsNow = datumList.get(idx);
                                //alltext.setText(HummingUtils.getSentence(datumsNow, playerMusicActivity));
                                //alltextkr.setText(HummingUtils.getSentenceLo(datumsNow, playerMusicActivity));
                                //speakko.setText(HummingUtils.getSpeakLo(datumsNow, playerMusicActivity));

                                if (runPositionProgress) {
                                    if(idx != 0){
                                        layoutManager.scrollToPositionWithOffset(idx - 1, 0);
                                    }
                                }
                            }
                            if (datumList.size() - idx < 5) {
                                es.actLoadMore();
                            }
                            break;
                        }
                    }
                }

            }
        }, 50);

    }

    public void maxProgress(int max) {
        songProgress.cancelAnimation();
        songProgress.setMaxWithAnim(max);
    }

   /* private void setPlayNextVideoButtonClickListener(final com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer youTubePlayer) {
        Button playNextVideoButton = view.findViewById(R.id.next_video_button);

        playNextVideoButton.setOnClickListener(view -> {
            String videoId = videoIds[new Random().nextInt(videoIds.length)];
            loadVideo(youTubePlayer, videoId);
        });
    }*/

    private void loadVideo(com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer youTubePlayer, String videoId, float startTime) {
        if(getLifecycle().getCurrentState() == Lifecycle.State.RESUMED)
            youTubePlayer.loadVideo(videoId, startTime);
        else
            youTubePlayer.cueVideo(videoId, startTime);

       // setVideoTitle(youTubePlayerView.getPlayerUIController(), videoId);
    }

    /**
     * This method adds a new custom action to the player.
     * Custom actions are shown next to the Play/Pause button in the middle of the player.
     */
    private void addCustomActionToPlayer(com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer youTubePlayer) {
        Drawable customActionIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_36dp);

        /*youTubePlayerView.getPlayerUIController().setCustomAction1(customActionIcon, view -> {
            if(youTubePlayer != null) youTubePlayer.pause();
        });*/
    }

    private void removeCustomActionFromPlayer() {
        youTubePlayerView.getPlayerUIController().showCustomAction1(false);
    }

    /**
     * Shows the menu button in the player and adds an item to it.
     */
    private void initPlayerMenu() {
        youTubePlayerView.getPlayerUIController().showMenuButton(false);
        /*youTubePlayerView.getPlayerUIController().getMenu().addItem(
                new MenuItem("example", R.drawable.ic_action_arrow_down, (view) -> Toast.makeText(getContext(), "item clicked", Toast.LENGTH_SHORT).show())
        );*/
    }

    @Override
    public void onBack() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    //    ((MainActivity) getActivity()).setOnKeyBackPressedListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void stopVideo() {
        if(youTubePlayer != null){
            youTubePlayer.pause();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity)getContext()).getBottomNavigation().setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity)getContext()).getBottomNavigation().setVisibility(View.VISIBLE);
    }
}