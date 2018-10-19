package com.test.english.ui.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.exam.english.R;
import com.exam.english.databinding.ActivityMainBinding;
import com.test.english.api.Datums;
import com.test.english.application.MyCustomApplication;
import com.test.english.ui.adapter.MainViewPagerAdapter;
import com.test.english.ui.frag1.Fragment1;
import com.test.english.ui.frag3.Fragment3;
import com.test.english.ui.frag4.Fragment4;
import com.test.english.ui.fragmentmusic.MusicFragment;
import com.test.english.ui.helper.BottomNavigationNotShiftHelper;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewPagerAdapter mainViewPagerAdapter;

    private int isSpeakLo = 0;

    //private DraggablePanel draggableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyCustomApplication application = (MyCustomApplication)getApplication();
        application.setMainInstance(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        BottomNavigationNotShiftHelper.disableShiftMode(binding.bottomNavigation);
        setupViewPager(binding.mainViewPager);

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

    /*public void setVideoUrl(final Datums datums) {

        if (getApplicationInstance().getConfig().getVideoMode().equals("youtube")) {
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
            getVideoListFragment().playSpeedButtonInit(initSpeed);
            BigDecimal pSpeed = new BigDecimal(initSpeed);
            pSpeed = pSpeed.divide(new BigDecimal(100), 1, BigDecimal.ROUND_UP);
            videoFragment.setPlaySpeed(pSpeed.floatValue());

            // video Url 설정
            videoFragment.setUrl(HummingUtils.IMAGE_PATH + datums.source.get(HummingUtils.ElasticField.VIDEO_URL), datums.source.get(HummingUtils.ElasticField.IDS).toString(), datums.source.get(HummingUtils.ElasticField.YOUTUBE_ID).toString());
            // text 설정
            videoFragment.setAlltext(datums.source.get(HummingUtils.ElasticField.TEXT_EN).toString(), datums.source.get(HummingUtils.ElasticField.TEXT_LO).toString(), "");
            videoListFragment.initPlayer();
            videoFragment.setBaseSentence(datums);
            MainActivity.SEARCH_POPUP_VALUE = datums.source.get(HummingUtils.ElasticField.TEXT_EN).toString();
            MainActivity.SEARCH_IDS_VALUE = datums.source.get(HummingUtils.ElasticField.IDS).toString();
            MainActivity.SEARCH_YOUTUBE_VALUE = datums.source.get(HummingUtils.ElasticField.YOUTUBE_ID).toString();
            MainActivity.SEARCH_YOUTUBE_CHANNEL_VALUE = datums.source.get(HummingUtils.ElasticField.YOUTUBE_CHANNEL_ID).toString();
            MainActivity.SEARCH_CHECK = true;

            // 썸네일 설정
            List<String> thumbnails = (List<String>) datums.source.get(HummingUtils.ElasticField.THUMBNAILS);
            videoListFragment.setThumbnails(thumbnails);
            changeIds = true;

            Handler handlers2 = new Handler();
            handlers2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (draggableView.isMaximized()) {
                        if (changeIds) {
                            changeIds = false;
                            videoListFragment.changeView();
                        }
                    }
                    postClick(MainActivity.SEARCH_IDS_VALUE);
                }
            }, 100);
        }
    }*/

    public void setupViewPager(ViewPager viewPager) {
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());

        mainViewPagerAdapter.addFragment(Fragment1.newInstance());
        mainViewPagerAdapter.addFragment(MusicFragment.newInstance());
        mainViewPagerAdapter.addFragment(Fragment3.newInstance());
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
}
