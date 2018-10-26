package com.test.english.ui.main;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.Voice;
import com.amazonaws.services.s3.AmazonS3Client;
import com.exam.english.R;
import com.exam.english.databinding.ActivityMainBinding;
import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.test.english.api.Datums;
import com.test.english.application.MyCustomApplication;
import com.test.english.ui.adapter.MainViewPagerAdapter;
import com.test.english.ui.frag1.ExploreFragment;
import com.test.english.ui.frag3.SearchFragment;
import com.test.english.ui.frag4.Fragment4;
import com.test.english.ui.fragmentmusic.MusicFragment;
import com.test.english.ui.helper.BottomNavigationNotShiftHelper;
import com.test.english.ui.youtube.MoviePosterFragment;
import com.test.english.ui.youtube.VideoFragment;
import com.test.english.ui.youtube.VideoListFragment;
import com.test.english.util.HummingUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewPagerAdapter mainViewPagerAdapter;

    private int isSpeakLo = 0;

    //YouTube
    private DraggablePanel draggableView;
    boolean changeIds = true;
    private YouTubePlayer youtubePlayer;
    private YouTubePlayerSupportFragment youtubeFragment;
    private static final String YOUTUBE_API_KEY = "AIzaSyC1rMU-mkhoyTvBIdTnYU0dss0tU9vtK48";
    private static final String VIDEO_KEY = "gsjtg7m1MMM";

    //YouTube Test
    private static final String VIDEO_POSTER_THUMBNAIL =
            "http://4.bp.blogspot.com/-BT6IshdVsoA/UjfnTo_TkBI/AAAAAAAAMWk/JvDCYCoFRlQ/s1600/"
                    + "xmenDOFP.wobbly.1.jpg";
    private static final String VIDEO_POSTER_TITLE = "X-Men: Days of Future Past";
    private static final String SECOND_VIDEO_POSTER_THUMBNAIL =
            "http://media.comicbook.com/wp-content/uploads/2013/07/x-men-days-of-future-past"
                    + "-wolverine-poster.jpg";

    private VideoFragment videoFragment;
    public static FragmentManager fm;
    public static String SEARCH_VALUE = "";
    public static String SEARCH_POPUP_VALUE = "";
    public static String SEARCH_IDS_VALUE = "";
    public static String SEARCH_YOUTUBE_VALUE = "";
    public static String SEARCH_YOUTUBE_CHANNEL_VALUE = "";
    public static boolean SEARCH_CHECK = false;

    public List<Voice> allvoices;
    public List<Voice> voices;
    private AmazonS3Client s3Client;
    private VideoListFragment videoListFragment;

    private onKeyBackPressedListener mOnKeyBackPressedListener;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private static final String COGNITO_POOL_ID = "ap-northeast-2:171a8c75-0910-4ce3-a178-81c79f3cf0a7";
    private static final Regions MY_REGION = Regions.AP_NORTHEAST_2;
    public AmazonPollyPresigningClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyCustomApplication application = (MyCustomApplication)getApplication();
        application.setMainInstance(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        BottomNavigationNotShiftHelper.disableShiftMode(binding.bottomNavigation);
        setupViewPager(binding.mainViewPager);

        draggableView = binding.draggablePanel;
        fm = getSupportFragmentManager();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                //if (getApplicationInstance().getConfig().getVideoMode().equals("youtube")) {
                    //initializeYoutubeFragment();
                    //initializeDraggablePanel();
                    //hookDraggablePanelListeners();
                //} else {
                    draggbleView();
                //}
            }
        }, 500);

        Handler handler4 = new Handler();
        handler4.postDelayed(new Runnable() {
            @Override public void run() {
                createPolly();
            }
        }, 0);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.frag1:
                        binding.mainViewPager.setCurrentItem(0);
                        return true;
                    case R.id.musicFragment:
                        binding.mainViewPager.setCurrentItem(1);
                        return true;
                    case R.id.frag3:
                        binding.mainViewPager.setCurrentItem(2);
                        return true;
                    case R.id.frag4:
                        binding.mainViewPager.setCurrentItem(3);
                        return true;
                }
                return false;
            }
        });

        binding.mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                binding.bottomNavigation.getMenu().getItem(position).setChecked(true);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void draggbleView() {

        draggableView.setFragmentManager(MainActivity.fm);
        videoFragment = new VideoFragment();
        videoFragment.setView(draggableView);
        videoListFragment = VideoListFragment.newInstance();
        draggableView.setTopFragment(videoFragment);
        draggableView.setBottomFragment(videoListFragment);

        draggableView.setDraggableListener(new DraggableListener() {
            @Override public void onMaximized() {
                /*if(musicFragment != null){
                    musicFragment.stopVideo();
                }*/
                videoFragment.onMaximized();

                // Relation Episode 리스트 새로고침
                if (changeIds) {
                    changeIds = false;
                    videoListFragment.changeView();
                }

                videoListFragment.showPanel();
                videoListFragment.showTab();
                /*if(videoListFragment.getSpeakButtonTag().equals("on")){
                    if(!menuRed.isOpened() && isMenuButton()){
                        menuRed.showMenuButton(true);
                    }
                    if(!mFab.isShown()){
                        mFab.show(true);
                    }
                }
                if(videoListFragment.getRecordButtonTag().equals("on")){
                    showSpeakButton(false);
                    showRecordButton(true);
                }*/

                /*if(!voiceBtn.isOpened()){
                    voiceBtn.showMenuButton(true);
                }*/
            }

            //Empty
            @Override public void onMinimized() {
                videoFragment.onMinimized();
                /*menuRed.hideMenuButton(true);
                voiceBtn.hideMenuButton(true);
                recordFab.hide(true);
                deleteFile();*/
            }

            @Override public void onClosedToLeft() {
                videoFragment.pauseVideo();
                /*menuRed.hideMenuButton(true);
                voiceBtn.hideMenuButton(true);
                mFab.hide(true);
                recordFab.hide(true);

                deleteFile();*/
                videoListFragment.collapsePanel();
            }

            @Override public void onClosedToRight() {
                videoFragment.pauseVideo();
                /*menuRed.hideMenuButton(true);
                voiceBtn.hideMenuButton(true);
                mFab.hide(true);
                recordFab.hide(true);
                deleteFile();*/
                videoListFragment.collapsePanel();
            }

            @Override
            public void onStartTouch() {
                videoFragment.onStartTouch();
            }
        });
        draggableView.setClickToMinimizeEnabled(false);
        draggableView.setClickToMaximizeEnabled(false);
        draggableView.initializeView();
        draggableView.setVisibility(View.INVISIBLE);
    }

    private void initializeYoutubeFragment() {
        youtubeFragment = new YouTubePlayerSupportFragment();
        youtubeFragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                          YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    youtubePlayer = player;
                    youtubePlayer.loadVideo(VIDEO_KEY);
                    youtubePlayer.setShowFullscreenButton(true);
                }
            }
            @Override public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                          YouTubeInitializationResult error) {
            }
        });
    }

    /**
     * Initialize and configure the DraggablePanel widget with two fragments and some attributes.
     */
    private void initializeDraggablePanel() {
        //draggablePanel.setFragmentManager(getSupportFragmentManager());
        //draggablePanel.setTopFragment(youtubeFragment);
        MoviePosterFragment moviePosterFragment = new MoviePosterFragment();
        moviePosterFragment.setPoster(VIDEO_POSTER_THUMBNAIL);
        moviePosterFragment.setPosterTitle(VIDEO_POSTER_TITLE);
        //draggablePanel.setBottomFragment(moviePosterFragment);
        //draggablePanel.initializeView();
        /*Picasso.with(this)
                .load(SECOND_VIDEO_POSTER_THUMBNAIL)
                .placeholder(R.drawable.ic_cake_black_24dp)
                .into(thumbnailImageView);*/
    }

    /**
     * Hook the DraggableListener to DraggablePanel to pause or resume the video when the
     * DragglabePanel is maximized or closed.
     */
    private void hookDraggablePanelListeners() {
        /*draggablePanel.setDraggableListener(new DraggableListener() {
            @Override public void onMaximized() {
                playVideo();
            }

            @Override public void onMinimized() {
                //Empty
            }

            @Override public void onClosedToLeft() {
                pauseVideo();
            }

            @Override public void onClosedToRight() {
                pauseVideo();
            }

            @Override
            public void onStartTouch() {

            }
        });*/
    }

    /**
     * Pause the video reproduced in the YouTubePlayer.
     */
    private void pauseVideo() {
        if (youtubePlayer.isPlaying()) {
            youtubePlayer.pause();
        }
    }

    /**
     * Resume the video reproduced in the YouTubePlayer.
     */
    private void playVideo() {
        if (!youtubePlayer.isPlaying()) {
            youtubePlayer.play();
        }
    }

    public void setVideoUrl(final Datums datums) {

        if (false) { //if (getApplicationInstance().getConfig().getVideoMode().equals("youtube")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override public void run() {
                    setYoutubeId(datums.source.get(HummingUtils.ElasticField.YOUTUBE_ID).toString(), 2000);
                }
            }, 10);
        } else {
            draggableView.setVisibility(View.VISIBLE);
            draggableView.minimize();

            // 재생 속도 초기화 : 100
            int initSpeed = 100;
            //getVideoListFragment().playSpeedButtonInit(initSpeed);
            BigDecimal pSpeed = new BigDecimal(initSpeed);
            pSpeed = pSpeed.divide(new BigDecimal(100), 1, BigDecimal.ROUND_UP);
            videoFragment.setPlaySpeed(pSpeed.floatValue());

            // video Url 설정
            videoFragment.setUrl(HummingUtils.IMAGE_PATH + datums.source.get(HummingUtils.ElasticField.VIDEO_URL), datums.source.get(HummingUtils.ElasticField.IDS).toString(), datums.source.get(HummingUtils.ElasticField.YOUTUBE_ID).toString());
            // text 설정
            videoFragment.setAlltext(datums.source.get(HummingUtils.ElasticField.TEXT_EN).toString(), datums.source.get(HummingUtils.ElasticField.TEXT_LO).toString(), "");
            //videoListFragment.initPlayer();
            videoFragment.setBaseSentence(datums);
            MainActivity.SEARCH_POPUP_VALUE = datums.source.get(HummingUtils.ElasticField.TEXT_EN).toString();
            MainActivity.SEARCH_IDS_VALUE = datums.source.get(HummingUtils.ElasticField.IDS).toString();
            MainActivity.SEARCH_YOUTUBE_VALUE = datums.source.get(HummingUtils.ElasticField.YOUTUBE_ID).toString();
            MainActivity.SEARCH_YOUTUBE_CHANNEL_VALUE = datums.source.get(HummingUtils.ElasticField.YOUTUBE_CHANNEL_ID).toString();
            MainActivity.SEARCH_CHECK = true;

            // 썸네일 설정
            List<String> thumbnails = (List<String>) datums.source.get(HummingUtils.ElasticField.THUMBNAILS);
            //videoListFragment.setThumbnails(thumbnails);
            changeIds = true;

            Handler handlers2 = new Handler();
            handlers2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (draggableView.isMaximized()) {
                        if (changeIds) {
                            changeIds = false;
                            //videoListFragment.changeView();
                        }
                    }
                    //postClick(MainActivity.SEARCH_IDS_VALUE);
                }
            }, 100);
        }
    }

    public void setupViewPager(ViewPager viewPager) {
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());

        mainViewPagerAdapter.addFragment(ExploreFragment.newInstance());
        mainViewPagerAdapter.addFragment(MusicFragment.newInstance());
        mainViewPagerAdapter.addFragment(SearchFragment.newInstance());
        mainViewPagerAdapter.addFragment(Fragment4.newInstance());

        binding.mainViewPager.setAdapter(mainViewPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setSpeakLo(int isSpeakLo){
        this.isSpeakLo = isSpeakLo;
    }

    /*
    0: speakLo, textLo
    1: speakLo, textEn
    2: textEn, textLo
    3: textEn, speakLo
    4: textLo, textEn
    5: textLo, speakLo
    6: textEn, title
     */
    public int getSpeakLo(){
        return isSpeakLo;
    }

    public void setYoutubeId(String youtubeId, int seek) {
        /*if(getApplicationInstance().getConfig().getVideoMode().equals("youtube")){
            draggablePanel.minimize();
            draggablePanel.setVisibility(View.VISIBLE);
            youtubePlayer.cueVideo(youtubeId);
            seekTime = seek;
            youtubePlayer.seekToMillis(seek);
        }*/
    }

    public VideoFragment getVideoFragment(){
        return videoFragment;
    }

    public void seekTo(int seek){
        videoFragment.seekTo(seek);
    }

    public String getVideoUrl() {
        return videoFragment.getVideoUrl();
    }

    public boolean doesObjectExist(String fileName) {
        return s3Client.doesObjectExist("humming", fileName);
    }

    public int getTotalDuration(){
        return videoFragment.getTotalDuration();
    }

    public void setVideoEpisodeUrl(final Datums datums, final int nowPlayListNo, boolean refresh) {

        changeIds = true;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override public void run() {

                videoFragment.setUrl(HummingUtils.IMAGE_PATH+datums.source.get(HummingUtils.ElasticField.VIDEO_URL), datums.source.get(HummingUtils.ElasticField.IDS).toString(), datums.source.get(HummingUtils.ElasticField.YOUTUBE_ID).toString());
                videoFragment.setAlltext(datums.source.get(HummingUtils.ElasticField.TEXT_EN).toString(), datums.source.get(HummingUtils.ElasticField.TEXT_LO).toString(), "");
                videoFragment.setNowPlayListNo(nowPlayListNo);
                MainActivity.SEARCH_POPUP_VALUE = datums.source.get(HummingUtils.ElasticField.TEXT_EN).toString();
                MainActivity.SEARCH_IDS_VALUE = datums.source.get(HummingUtils.ElasticField.IDS).toString();

                // 썸네일 설정
                List<String> thumbnails = (List<String>) datums.source.get(HummingUtils.ElasticField.THUMBNAILS);
                videoListFragment.setThumbnails(thumbnails);

                if(true){//if(refresh){
                    videoFragment.setBaseSentence(datums);
                    Handler handlers3 = new Handler();
                    handlers3.postDelayed(new Runnable() {
                        @Override public void run() {
                            //refreshRelation();
                        }
                    }, 100);
                }


                Handler handlers2 = new Handler();
                handlers2.postDelayed(new Runnable() {
                    @Override public void run() {
                        //postClick(MainActivity.SEARCH_IDS_VALUE);
                    }
                }, 100);
            }
        }, 10);
    }

    public interface onKeyBackPressedListener {
        public void onBack();
    }

    public void setOnKeyBackPressedListener(onKeyBackPressedListener listener) {
        mOnKeyBackPressedListener = listener;
    }

    public VideoListFragment getVideoListFragment(){
        return videoListFragment;
    }

    public Datums getBaseSentence() {
        return videoFragment.getBaseSentence();
    }

    public void createPolly() {
        initPollyClient();
        new GetPollyVoices().execute();
    }

    void initPollyClient() {
        credentialsProvider = new CognitoCachingCredentialsProvider( this, COGNITO_POOL_ID,  MY_REGION );
        client = new AmazonPollyPresigningClient(credentialsProvider);
    }

    private class GetPollyVoices extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (allvoices != null) {
                return null;
            }

            // Create describe voices request.
            DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

            DescribeVoicesResult describeVoicesResult;
            try {
                // Synchronously ask the Polly Service to describe available TTS voices.
                describeVoicesResult = client.describeVoices(describeVoicesRequest);
            } catch (RuntimeException e) {
                Log.e("", "Unable to get available voices. " + e.getMessage());
                return null;
            }

            // Get list of voices from the result.
            allvoices = describeVoicesResult.getVoices();

            // Log a message with a list of available TTS voices.
            Log.i("", "Available Polly voices: " + voices);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            voices = new ArrayList<>();
            for (int i = 0; i < allvoices.size(); i++) {
                if(allvoices.get(i).getLanguageCode().equals("en-US")
                        || allvoices.get(i).getLanguageCode().equals("en-GB")
                        || allvoices.get(i).getLanguageCode().equals("en-AU")
                        ){
                    voices.add(allvoices.get(i));
                }
            }
        }
    }
}