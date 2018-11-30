package com.edxdn.hmsoon.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.Voice;
import com.amazonaws.services.s3.AmazonS3Client;
import com.edxdn.hmsoon.ui.fragmentcommon.ContentsFragment;
import com.edxdn.hmsoon.ui.fragmentcommon.ConversationFragment;
import com.edxdn.hmsoon.ui.youtube.PlayMusicFragment;
import com.edxdn.hmsoon.R;
import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.application.MyCustomApplication;
import com.edxdn.hmsoon.ui.adapter.BackPressCloseHandler;
import com.edxdn.hmsoon.ui.adapter.ViewPagerAdapter;
import com.edxdn.hmsoon.ui.fragmentcommon.MoreFragment;
import com.edxdn.hmsoon.ui.fragmentcommon.PatternFragment;
import com.edxdn.hmsoon.ui.fragmentexplore.ExploreFragment;
import com.edxdn.hmsoon.ui.fragmentmypage.MyPageFragment;
import com.edxdn.hmsoon.ui.fragmentsearch.SearchBeforeHandFragment;
import com.edxdn.hmsoon.ui.fragmentmusic.MusicFragment;
import com.edxdn.hmsoon.ui.helper.BottomNavigationNotShiftHelper;
import com.edxdn.hmsoon.ui.fragmentsearch.SearchFragment;
import com.edxdn.hmsoon.ui.youtube.MoviePosterFragment;
import com.edxdn.hmsoon.ui.youtube.VideoFragment;
import com.edxdn.hmsoon.ui.youtube.VideoListFragment;
import com.edxdn.hmsoon.util.HummingUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
    public static FragmentManager fragmentManager;
    public static String SEARCH_VALUE = "";
    public static String SEARCH_POPUP_VALUE = "";
    public static String SEARCH_PAGE_VALUE = "";
    public static String SEARCH_IDS_VALUE = "";
    public static String SEARCH_YOUTUBE_VALUE = "";
    public static String SEARCH_YOUTUBE_CHANNEL_VALUE = "";
    public static boolean SEARCH_CHECK = false;

    public List<Voice> allvoices;
    public List<Voice> voices;
    private AmazonS3Client s3Client;
    private VideoListFragment videoListFragment;

    private onKeyBackPressedListener mOnKeyBackPressedListener;
    private BackPressCloseHandler backPressCloseHandler;

    private CognitoCachingCredentialsProvider credentialsProvider;
    private static final String COGNITO_POOL_ID = "ap-northeast-2:171a8c75-0910-4ce3-a178-81c79f3cf0a7";
    private static final Regions MY_REGION = Regions.AP_NORTHEAST_2;
    public AmazonPollyPresigningClient client;

    private static final String TAG_HOME = "home";
    private static final String TAG_MOVIES = "movies";

    public static int CURRENT_DEPTH = 0;
    public static String CURRENT_TAG = TAG_HOME;
    public static String CURRENT_TITLE = "";
    public static String CURRENT_ICODE = "";
    public static String CURRENT_RCODE = "";
    public static String CURRENT_TAG2 = "";
    public static String CURRENT_TITLE2 = "";

    public static int navItemIndex = 0;
    private Fragment mExplorefragment;
    private Fragment mMusicfragment;
    private Fragment mSearchfragment;
    private Fragment mMypagefragment;

    public static int EXPLORE_FRAGMENT = 0;
    public static int MUSIC_FRAGMENT = 1;
    public static int SEARCH_FRAGMENT = 3;
    public static int MYPAGE_FRAGMENT = 4;

    private BottomNavigationView bottomNavigation;
    private DraggablePanel draggable_panel;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private LinearLayout toolbarlayout;
    private LinearLayout toolbarlayout2;
    private TextView searchText;
    private TextView searchText2;

    private PlayMusicFragment mPlayMusicFragment;

    public static final int REQUEST_RECORD_AUDIO = 110;
    private static final int REQUEST_SEARCH = 400;

    private List<Datums> favoriteList;
    private ViewPager viewPager;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExplorefragment = new ExploreFragment().newInstance();
        mMusicfragment = new MusicFragment().newInstance();
        mSearchfragment = new SearchBeforeHandFragment().newInstance();
        mMypagefragment = new MyPageFragment().newInstance();

        MyCustomApplication application = (MyCustomApplication)getApplication();
        application.setMainInstance(this);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        HummingUtils.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        HummingUtils.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        draggable_panel = (DraggablePanel) findViewById(R.id.draggable_panel);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        //toolbarlayout = (LinearLayout) findViewById(R.id.toolbarlayout);
        toolbarlayout2 = (LinearLayout) findViewById(R.id.toolbarlayout2);
        //searchText = (TextView) findViewById(R.id.searchText);
        searchText2 = (TextView) findViewById(R.id.searchText2);

        BottomNavigationNotShiftHelper.disableShiftMode(bottomNavigation);
        backPressCloseHandler = new BackPressCloseHandler(this);

        draggableView = draggable_panel;
        fragmentManager = getSupportFragmentManager();

        setSupportActionBar(toolbar);

        //binding.toolbarlayout.setVisibility(View.GONE);

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

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (!CURRENT_TAG.equals(TAG_HOME)) {
                    Drawable drawable= getResources().getDrawable(R.drawable.icon_back);
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, (int) convertDpToPixel(11, MainActivity.this), (int) convertDpToPixel(20, MainActivity.this), true));
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeAsUpIndicator(newdrawable);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                    //toolbarlayout.setVisibility(View.GONE);
                    toolbarlayout2.setVisibility(View.VISIBLE);
                    searchText2.setText(CURRENT_TITLE);
                } /*else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setDisplayShowHomeEnabled(false);
                    toolbarlayout.setVisibility(View.VISIBLE);
                    toolbarlayout2.setVisibility(View.GONE);
                    searchText2.setText("");
                }*/
            }
        });

        //replaceFragment();

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.explore:
                        navItemIndex = EXPLORE_FRAGMENT;
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.musicFragment:
                        navItemIndex = MUSIC_FRAGMENT;
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.search:
                        navItemIndex = SEARCH_FRAGMENT;
                        viewPager.setCurrentItem(2);
                        return true;
                    case R.id.mypage:
                        navItemIndex = MYPAGE_FRAGMENT;
                        viewPager.setCurrentItem(3);
                        return true;
                    default:
                        replaceFragment();
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigation.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mExplorefragment);
        adapter.addFragment(mMusicfragment);
        adapter.addFragment(mSearchfragment);
        adapter.addFragment(mMypagefragment);
        viewPager.setAdapter(adapter);
    }

    public void draggbleView() {

        draggableView.setFragmentManager(MainActivity.fragmentManager);
        videoFragment = new VideoFragment();
        videoFragment.setView(draggableView);
        //videoListFragment = VideoListFragment.newInstance();
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
                bottomNavigation.setVisibility(View.GONE);
            }

            //Empty
            @Override public void onMinimized() {
                videoFragment.onMinimized();
                /*menuRed.hideMenuButton(true);
                voiceBtn.hideMenuButton(true);
                recordFab.hide(true);
                deleteFile();*/
                bottomNavigation.setVisibility(View.GONE);
            }

            @Override public void onClosedToLeft() {
                videoFragment.pauseVideo();
                /*menuRed.hideMenuButton(true);
                voiceBtn.hideMenuButton(true);
                mFab.hide(true);
                recordFab.hide(true);

                deleteFile();*/
                videoListFragment.collapsePanel();
                bottomNavigation.setVisibility(View.VISIBLE);
            }

            @Override public void onClosedToRight() {
                videoFragment.pauseVideo();
                /*menuRed.hideMenuButton(true);
                voiceBtn.hideMenuButton(true);
                mFab.hide(true);
                recordFab.hide(true);
                deleteFile();*/
                videoListFragment.collapsePanel();
                bottomNavigation.setVisibility(View.VISIBLE);
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
            videoListFragment.setAlltext(datums.source.get(HummingUtils.ElasticField.TEXT_EN).toString(), datums.source.get(HummingUtils.ElasticField.TEXT_LO).toString(), "");
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

    @Override
    public void onBackPressed() {

        if (mOnKeyBackPressedListener != null) {
            mOnKeyBackPressedListener.onBack();
        } else if (draggableView.getVisibility() == View.VISIBLE && draggableView.isMaximized()) {
            draggableView.minimize();
        } else if (draggableView.getVisibility() == View.VISIBLE && draggableView.isMinimized()) {
            draggableView.closeToRight();
        } else if (CURRENT_DEPTH == 2) {
            CURRENT_TAG = CURRENT_TAG2;
            CURRENT_TITLE = CURRENT_TITLE2;
            CURRENT_DEPTH = 1;
            super.onBackPressed();
        } else if (CURRENT_TAG != TAG_HOME) {
            CURRENT_TAG = TAG_HOME;
            CURRENT_TAG2 = "";
            CURRENT_TITLE2 = "";
            CURRENT_DEPTH = 0;
            super.onBackPressed();
        } else {
            backPressCloseHandler.onBackPressed();
        }
        appBarLayout.setVisibility(View.GONE);
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
                videoListFragment.setAlltext(datums.source.get(HummingUtils.ElasticField.TEXT_EN).toString(), datums.source.get(HummingUtils.ElasticField.TEXT_LO).toString(), "");
                videoFragment.setNowPlayListNo(nowPlayListNo);
                MainActivity.SEARCH_POPUP_VALUE = datums.source.get(HummingUtils.ElasticField.TEXT_EN).toString();
                MainActivity.SEARCH_IDS_VALUE = datums.source.get(HummingUtils.ElasticField.IDS).toString();

                // 썸네일 설정
                List<String> thumbnails = (List<String>) datums.source.get(HummingUtils.ElasticField.THUMBNAILS);
                videoListFragment.setThumbnails(thumbnails);

                if (true) {//if(refresh){
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
                if (allvoices.get(i).getLanguageCode().equals("en-US")
                        || allvoices.get(i).getLanguageCode().equals("en-GB")
                        || allvoices.get(i).getLanguageCode().equals("en-AU")
                        ) {
                    voices.add(allvoices.get(i));
                }
            }
        }
    }

    public void showToolbar() {
        appBarLayout.setVisibility(View.VISIBLE);
    }

    public void onClickItems(String type, String sentence) {
        if (type.equals("type1") && sentence.equals("sentence")) {
            openPage("type1", sentence);
        } else if (type.equals("type1") && sentence.equals("pattern")) {
            openPage("type1", sentence);
        } else if (type.equals("latestReleases")) {
            openPage("latestReleases", sentence);
        } else if (type.equals("sentences")) {
            openPage("sentences", sentence);
        } else if (type.equals("patterns")) {
            openPage("patterns", sentence);
        } else if (type.equals("genres")) {
            openPage("genres", sentence);
        } else if (type.equals("words")) {
            openPage("words", sentence);
        } else if (type.equals("popular")) {
            openPage("popular", sentence);
        } else if (type.equals("favorite")) {
            openPage("favorite", sentence);
        } else if (type.equals("watched")) {
            openPage("watched", sentence);
        } else if (type.equals("mothergoose")) {
            openPage("mothergoose", sentence);
        }
    }

    public void openPage(String type, String sentence ) {
        sentence = sentence.replaceAll("_", " ");
        CURRENT_DEPTH = 1;

        if (!CURRENT_TAG.equals(TAG_HOME)) {
            CURRENT_TITLE2 = CURRENT_TITLE;
            CURRENT_DEPTH = 2;
        }

        SEARCH_PAGE_VALUE = "";
        if (type.equals("type1") && sentence.equals("sentence")) {
            navItemIndex = 11;
            CURRENT_TAG = TAG_MOVIES;
        } else if (type.equals("type1") && sentence.equals("pattern")) {
            navItemIndex = 2;
            CURRENT_TAG = TAG_MOVIES;
        } else if (type.equals("latestReleases")) {
            navItemIndex = 2;
            CURRENT_TAG = TAG_MOVIES;
        } else if (type.equals("sentences")) {
            SEARCH_PAGE_VALUE = sentence;
            navItemIndex = 2;
            CURRENT_TAG = TAG_MOVIES;
            CURRENT_TITLE = "Sentences";
            if (!sentence.equals("")) {
                CURRENT_TITLE = sentence;
            }
        } else if (type.equals("patterns")) {
            navItemIndex = 12;
            CURRENT_TAG = TAG_MOVIES;
            CURRENT_TITLE = "Patterns";
        } else if(type.equals("genres")) {
            navItemIndex = 14;
            CURRENT_TAG = TAG_MOVIES;
            if (!sentence.equals("")) {
            }
        } else if(type.equals("words")) {
            navItemIndex = 13;
            CURRENT_TAG = TAG_MOVIES;
        } else if(type.equals("popular")) {
            navItemIndex = 11;
            CURRENT_TAG = TAG_MOVIES;
        } else if(type.equals("favorite")) {
            navItemIndex = 17;
            CURRENT_TAG = TAG_MOVIES;
        } else if(type.equals("watched")) {
            navItemIndex = 18;
            CURRENT_TAG = TAG_MOVIES;
        } else if(type.equals("music")) {
            navItemIndex = 19;
            CURRENT_TAG = TAG_MOVIES;
        } else if(type.equals("mothergoose")) {
            navItemIndex = 20;
            CURRENT_TAG = TAG_MOVIES;
        }
        replaceFragment();
    }

    private void replaceFragment() {

        Fragment fragment = getReplaceFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (CURRENT_TAG.equals(TAG_HOME)) {
            fragmentTransaction
                    .replace(R.id.flContainer, fragment);
        } else {
            fragmentTransaction
                    .replace(R.id.flContainer, fragment)
                    .addToBackStack(null);
        }
        fragmentTransaction
                .commit();
    }

    private Fragment getReplaceFragment() {

        switch (navItemIndex) {
            case 0:
                return mExplorefragment;
            case 1:
                return mMusicfragment;
            case 2:
                MoreFragment moreFragment = new MoreFragment();
                return moreFragment;
            case 3:
                return mSearchfragment;
            case 4:
                return mMypagefragment;
            /*
            case 5:
                // settings fragment
                homeFragment = new HomeFragment();
                Bundle args = new Bundle();
                args.putString("param1", currentQuery);
                args.putString("param2", "param1");
                homeFragment.setArguments(args);
                return homeFragment;
            case 6:
                //MainFragment mainFragment = new MainFragment();
                //return mainFragment;
                libFragment = new LibraryFragment();
                return libFragment;
            case 7:
                //MainFragment mainFragment = new MainFragment();
                //return mainFragment;
                libFragment = new LibraryFragment();
                return libFragment;
            case 8:
                VerticalFragment verticalFragment = new VerticalFragment();
                return verticalFragment;
            */
            case 9:
                ConversationFragment conversationFragment = new ConversationFragment();
                return conversationFragment;
            /*
            case 10:
                PagerFragment pagerFragment = new PagerFragment();
                return pagerFragment;
            case 11:
                PopularFragment popularFragment = new PopularFragment();
                return popularFragment; */
            case 12:
                PatternFragment patternFragment = new PatternFragment();
                return patternFragment;
            /*case 13:
                WordFragment wordFragment = new WordFragment();
                return wordFragment;
            case 14:
                GenreFragment genreFragment = new GenreFragment();
                return genreFragment;
            case 15:
                RankFragment rankFragment = new RankFragment();
                return rankFragment;*/
            case 16:
                SearchFragment searchresultFragment = new SearchFragment();
                return searchresultFragment;
            /*case 17:
                FavoriteFragment favoriteFragment = new FavoriteFragment();
                return favoriteFragment;
            case 18:
                WatchedFragment watchedFragment = new WatchedFragment();
                return watchedFragment; */
            case 19:
                mPlayMusicFragment = new PlayMusicFragment();
                return mPlayMusicFragment;
            case 20:
                ContentsFragment contentsFragment = new ContentsFragment();
                return contentsFragment;
            /*
            default:
                libFragment = new LibraryFragment();
                return libFragment;*/
        }
        return null;
    }

    public void searchSentencePopup(final String sentences) {

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override public void run() {
                //postSearchHistory(sentences);
            }
        }, 500);

        //appBarLayout.setVisibility(View.VISIBLE);
        //searchText.setText(sentences);

        if (!sentences.equals("")) {

            MainActivity.SEARCH_CHECK = false;
            SEARCH_VALUE = sentences;
            navItemIndex = 16;
            CURRENT_TAG = TAG_MOVIES;
            CURRENT_TITLE = sentences;

            replaceFragment();
        }
    }

    public String checkFavorite(String videoIds) {
        String check = "";
        if (favoriteList != null && favoriteList.size() != 0) {
            for (int i = 0; i < favoriteList.size(); i++) {
                if(favoriteList.get(i).source.get(HummingUtils.ElasticField.IDS).equals(videoIds)){
                    check = favoriteList.get(i).id;
                    break;
                }
            }
        }
        return check;
    }

    public void setFavoriteList(List<Datums> favoriteList) {
        this.favoriteList = favoriteList;
    }

    public void openInterestPage(int type, Datums datums) {
        if (type == 1) {
            navItemIndex = 9;
            CURRENT_TAG = TAG_MOVIES;
            CURRENT_TITLE = datums.source.get(HummingUtils.ElasticField.TITLE).toString();
            CURRENT_ICODE = datums.source.get(HummingUtils.ElasticField.ICODE).toString();
        }
        replaceFragment();
    }

    public void setNextVideo(int nowPosition) {
        videoListFragment.setNextVideo(nowPosition, true);
    }

    public void setMusicVideoUrl(final Datums datums) {
        MainActivity.SEARCH_IDS_VALUE = datums.source.get(HummingUtils.ElasticField.IDS).toString();
        MainActivity.SEARCH_YOUTUBE_VALUE = datums.source.get(HummingUtils.ElasticField.YOUTUBE_ID).toString();
        MainActivity.SEARCH_YOUTUBE_CHANNEL_VALUE = datums.source.get(HummingUtils.ElasticField.YOUTUBE_CHANNEL_ID).toString();
        openPage("music", datums.source.get(HummingUtils.ElasticField.TITLE).toString());
    }

    public BottomNavigationView getBottomNavigation() {
        return bottomNavigation;
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);

        return (int)px;
    }

}