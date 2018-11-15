package com.test.english.ui.youtube;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.ColorRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.exam.english.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.test.english.api.Datums;
import com.test.english.record.CustomSTT;
import com.test.english.ui.adapter.ProgressBar;
import com.test.english.ui.main.MainActivity;
import com.test.english.util.HummingUtils;
import com.test.english.video.TimeLineViewMini;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

@SuppressLint("ValidFragment")
public class VideoListFragment extends Fragment {

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private TextView totalDur, otherVoices, currentDur;
    private Button textButton, texttButton, textsButton;
    private ImageView repeatOne, repeat, playSpeed, shuffle, rewind, forward, smallArtworkDown, prev, play, next;

   // private ImageView smallPrev;
    private ImageButton smallToggle, smallTexta, smallTextt;
  //  private ImageView smallNext;
    private ProgressBar songProgress;
    private AppCompatSeekBar seekbar;

    /*private Button recordPlayButton1;
    private Button recordPlayButton2;
    private Button recordPlayButton3;*/

    private LinearLayout linearLayout, smallLinearView, controlsBg;
    private VideoListFragmentPageAdapter mAdapterViewPager;
    private MainActivity mainActivity;
    private TimeLineViewMini timeLineView;
    private File file;
    private List<String> thumbnails;
    private int totalDuration = 0;
    private Thread mLoadSoundFileThread;
    private CustomSTT customSTT;
    private final int REQ_CODE_SPEECH = 100;
    private MediaRecorder mRecorder;
    private long mStartTime = 0;
    private int[] amplitudes = new int[100];
    private int iii = 0;
    private File mOutputFile;
    private int recodingCnt = 0;
    private Handler mHandler = new Handler();
    private Handler mHandler2 = new Handler();
    /*private Runnable mTickExecutor = new Runnable() {
        @Override
        public void run() {
            tick();
            mHandler.postDelayed(mTickExecutor,1000);
        }
    };*/

    private MediaPlayer player;

    private SlidingUpPanelLayout mLayout;
    private FrameLayout timeFrame;
    private MediaPlayer mediaPlayer;
    int voiceIdx = 0;
    boolean palyTfOtherVoice = true;

    List<String> filePAthList = new ArrayList<>();
    List<String> fileNameList = new ArrayList<>();

    public static VideoListFragment newInstance() {
        MainActivity.SEARCH_POPUP_VALUE = MainActivity.SEARCH_VALUE;
        return new VideoListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                for (int i = 0; i < recodingCnt; i++) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                            + "/Voice Recorder/RECORDING_"
                            + i
                            + ".amr");
                    if(file.exists()){
                        file.delete();
                    }
                }
            }
        }, 100);

        final View view = inflater.inflate(R.layout.fragment_videolist, container, false);
        mainActivity = (MainActivity) getActivity();
        tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        viewPager = (ViewPager)view.findViewById(R.id.view_pager);
        appBarLayout = (AppBarLayout)view.findViewById(R.id.id_appbar);

        mAdapterViewPager = new VideoListFragmentPageAdapter(getChildFragmentManager(), mainActivity);
        viewPager.setAdapter(mAdapterViewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Log.d("test","-----------======--------2222="+position);
                //mAdapterViewPager.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(0);

        customSTT = new CustomSTT(getActivity(), new CustomSTT.CallListener() {
            @Override
            public void speechStatus(String status) {
                if(status.equals("onResults")
                        || status.equals("onError")){
                }
            }

            @Override
            public void speechResult(String speak) {
                setSpeakResult(speak);
            }
        }, "en-US");

        timeLineView = view.findViewById(R.id.timeLineView);

        timeLineView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        int width = view.getWidth();
                        float x = motionEvent.getX();
                        float y = motionEvent.getY();
                        int rateX = (int) (x/width * 100);
                        mainActivity.seekTo(totalDuration * rateX/100);
                        mainActivity.getVideoFragment().startVideo();
                        return true;
                }
                return false;
            }

        });

        totalDur = (TextView) view.findViewById(R.id.totalDur);
        currentDur = (TextView) view.findViewById(R.id.currentDur);

        textButton = (Button) view.findViewById(R.id.text);
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                mainActivity.getVideoFragment().showHideText();
            }
        });
        texttButton = (Button) view.findViewById(R.id.textt);
        texttButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                mainActivity.getVideoFragment().showHideTextKr();
            }
        });
        textsButton = (Button) view.findViewById(R.id.texts);
        textsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                mainActivity.getVideoFragment().showHideTextSpeak();
            }
        });

        smallTexta = (ImageButton) view.findViewById(R.id.small_texta);
        smallTexta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                mainActivity.getVideoFragment().showHideText();
            }
        });
        smallTextt = (ImageButton) view.findViewById(R.id.small_textt);
        smallTextt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                mainActivity.getVideoFragment().showHideTextKr();
            }
        });
        /*textButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View p0) {
                mainActivity.getVideoFragment().showHideText();
            }
        });*/

        prev = (ImageView) view.findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                setNextVideo(mainActivity.getVideoFragment().getNowPlayListNo(),false);
            }
        });
        play = (ImageView) view.findViewById(R.id.play_pause_toggle);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                mainActivity.getVideoFragment().startVideoIfStop();
            }
        });
        next = (ImageView) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                setNextVideo(mainActivity.getVideoFragment().getNowPlayListNo(), true);
            }
        });

        /*smallPrev = (ImageView) view.findViewById(R.id.small_prev);
        smallPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                setNextVideo(mainActivity.getVideoFragment().getNowPlayListNo(),false);
            }
        });
        smallPrev.setVisibility(View.GONE);*/
        smallToggle = (ImageButton) view.findViewById(R.id.small_toggle);
        smallToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                mainActivity.getVideoFragment().startVideoIfStop();
            }
        });
        /*smallNext = (ImageView) view.findViewById(R.id.small_next);
        smallNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                setNextVideo(mainActivity.getVideoFragment().getNowPlayListNo(), true);
            }
        });
        smallNext.setVisibility(View.GONE);*/

        playSpeed = (ImageView) view.findViewById(R.id.playSpeed);
        playSpeed.setTag("100");
        playSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playSpeed.getTag().toString().equals("100")) {
                    playSpeed(120);
                    playSpeed.setTag("120");
                    playSpeed.setImageResource(R.drawable.icon_x_120);
                } else if (playSpeed.getTag().toString().equals("120")) {
                    playSpeed(40);
                    playSpeed.setTag("40");
                    playSpeed.setImageResource(R.drawable.icon_x_40);
                } else if (playSpeed.getTag().toString().equals("40")) {
                    playSpeed(60);
                    playSpeed.setTag("60");
                    playSpeed.setImageResource(R.drawable.icon_x_60);
                } else if (playSpeed.getTag().toString().equals("60")) {
                    playSpeed(80);
                    playSpeed.setTag("80");
                    playSpeed.setImageResource(R.drawable.icon_x_80);
                } else if (playSpeed.getTag().toString().equals("80")) {
                    playSpeed(100);
                    playSpeed.setTag("100");
                    playSpeed.setImageResource(R.drawable.icon_x_100);
                }
            }
        });

        repeatOne = (ImageView) view.findViewById(R.id.repeatOne);
        repeatOne.setTag("1");
        repeatOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                if(repeatOne.getTag().toString().equals("1")){
                    repeatOne.setTag("2");
                    repeatOne.setImageResource(R.drawable.ic_action_repeatx2);
                }else if(repeatOne.getTag().toString().equals("2")){
                    repeatOne.setTag("3");
                    repeatOne.setImageResource(R.drawable.ic_action_repeatx3);
                }else if(repeatOne.getTag().toString().equals("3")){
                    repeatOne.setTag("1");
                    repeatOne.setImageResource(R.drawable.ic_action_repeatx1);
                }
                mainActivity.getVideoFragment().initNowPlayCnt();

            }
        });
        repeat = (ImageView) view.findViewById(R.id.repeat);
        repeat.setTag("all");
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                if(repeat.getTag().toString().equals("all")){
                    repeat.setTag("one");
                    repeat.setImageResource(R.drawable.icon_replay_one);
                } else if (repeat.getTag().toString().equals("one")) {
                    repeat.setTag("all");
                }
            }
        });
        shuffle = (ImageView) view.findViewById(R.id.shuffle);
        shuffle.setTag("off");
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                if(shuffle.getTag().toString().equals("off")){
                    shuffle.setTag("on");
                    shuffle.setImageResource(R.drawable.icon_shuffle_active);
                }else{
                    shuffle.setTag("off");
                    shuffle.setImageResource(R.drawable.icon_shuffle);

                }
            }
        });

        rewind = (ImageView) view.findViewById(R.id.rewind);
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                mainActivity.getVideoFragment().seekToCurrent(-1000);
                updateProgress(mainActivity.getVideoFragment().getCurrentPosition());
            }
        });

        forward = (ImageView) view.findViewById(R.id.forward);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                mainActivity.getVideoFragment().seekToCurrent(1000);
                updateProgress(mainActivity.getVideoFragment().getCurrentPosition());
            }
        });
        //forward.setVisibility(View.GONE);

        smallArtworkDown = (ImageView) view.findViewById(R.id.small_artwork_down);
        smallArtworkDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p0) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        mLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        smallLinearView = (LinearLayout) view.findViewById(R.id.small_lenear_view);
        timeFrame = (FrameLayout) view.findViewById(R.id.timeFrame);
        timeFrame.setVisibility(View.GONE);

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                setMiniPlayerAlphaProgress(panel, slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                switch (newState) {
                    case COLLAPSED:
                        onPanelCollapsed(panel);
                        break;
                    case EXPANDED:
                        onPanelExpanded(panel);
                        break;
                    case ANCHORED:
                        collapsePanel(); // this fixes a bug where the panel would get stuck for some reason
                        break;
                }
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        songProgress = (ProgressBar) view.findViewById(R.id.songProgress);
        songProgress.setMax(100);
        songProgress.setProgressWithAnim(0);
        seekbar = (AppCompatSeekBar) view.findViewById(R.id.seekbar);
        seekbar.setMax(100);
        seekbar.setProgress(0);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mainActivity.seekTo(seekBar.getProgress());
                mainActivity.getVideoFragment().startVideo();
            }
        });

        controlsBg = (LinearLayout) view.findViewById(R.id.controls);
        controlsBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        linearLayout = view.findViewById(R.id.dragView);
        otherVoices = (TextView) view.findViewById(R.id.otherVoices);

        createPolly();

        return view;
    }

    public static class VideoListFragmentPageAdapter extends FragmentPagerAdapter {

        private static final String TAG = VideoListFragmentPageAdapter.class.getSimpleName();

        private static final int FRAGMENT_COUNT = 2;
        private Map<Integer, String> mFragmentTags;
        private FragmentManager mFragmentManager;
        private Context context;

        public VideoListFragmentPageAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentManager = fm;
            mFragmentTags = new HashMap<Integer, String>();
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return RelationFragment.newInstance();
                case 1:
                    return EpisodeFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {

            return POSITION_NONE;

       /* Fragment fragment = (Fragment) object;
        String tag = fragment.getTag();
        String last = tag.charAt(tag.length() - 1)+"";
        if (fullChange) {
            return POSITION_NONE;
        } else {
            fullChange = true;
            if(relationTag.equals(tag)){
                return super.getItemPosition(object);
            }else{
                return POSITION_NONE;
            }
        }*/
        }

        @Override
        public int getCount() {
            return FRAGMENT_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return context.getString(R.string.player_relation);
                case 1:
                    return context.getString(R.string.player_episode);
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object object = super.instantiateItem(container, position);
            if (object instanceof Fragment) {
                Fragment fragment = (Fragment) object;
                String tag = fragment.getTag();
                mFragmentTags.put(position, tag);
            }
            return object;
        }

        public Fragment getFragment(int position) {
            Fragment fragment = null;
            String tag = mFragmentTags.get(position);
            if (tag != null) {
                fragment = mFragmentManager.findFragmentByTag(tag);
            }
            return fragment;
        }
    }

    /*public void downMp4(InputStream is, String textToRead, String voiceId){
        Log.e("test", "bbbbbbbbbbbbbbbbb2");

        Thread mLoadSoundFileThread2 = new Thread() {
            public void run() {
                ContextWrapper cw = new ContextWrapper(mainActivity);
                File directory = cw.getDir("learnVideo", Context.MODE_PRIVATE);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                String fileName = voiceId + "_" + HummingUtils.removeSpecialCharVoice(textToRead).replaceAll(" ", "_");
                fileNameList.add(fileName);

                File f = new File(directory, fileName+".mp3");
                if (!f.exists()) {
        //            throw new java.io.FileNotFoundException(urlStr);
                }
                try {
                    FileUtils.copyInputStreamToFile(is, f);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("test", "bbbbbbbbbbbbbbbbb2");
                filePAthList.add(f.getPath());
                Runnable runnable = new Runnable() {
                    public void run() {
                        mainActivity.fileUploadS3Voice(fileNameList.get(filePAthList.size()-1)+".mp3",filePAthList.get(filePAthList.size()-1));
                    }
                };
                mHandler.post(runnable);
            }
        };
        mLoadSoundFileThread2.start();

    }*/

    public void finishOpeningSoundFile(){
//        timeLineView.setVideo(Uri.fromFile(new File(filePAth)));

        timeLineView.setVideo(mainActivity.getVideoUrl());
        timeLineView.setThumbnailList(thumbnails);
        timeLineView.setBitmap(timeLineView.getWidth(), getContext());
    }

    public void setThumbnails(List<String> thumbnails) {
        this.thumbnails = thumbnails;
    }

    public void createPolly() {

        setupNewMediaPlayer();

        /*otherVoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(palyTfOtherVoice){

                    palyTfOtherVoice = false;
                    Voice selectedVoice = (Voice) mainActivity.voices.get(voiceIdx);

                    String textToRead = mainActivity.getVideoFragment().getAllText("allText");

                    String fileName =  "voice/polly/"+selectedVoice.getId()+"_"+HummingUtils.removeSpecialCharVoice(textToRead).replaceAll(" ", "_")+".mp3";

                    Thread mLoadSoundFileThread = new Thread() {
                        public void run() {
                            boolean exists = mainActivity.doesObjectExist(fileName);
                            String voiceUrl = "";
                            Log.e("test", "+++++++++++++++++++++"+exists);
                            if (!exists) {
                                SynthesizeSpeechRequest synthesizeSpeechRequest =  new SynthesizeSpeechRequest()
                                        // Set text to synthesize.
                                        .withText(textToRead)
                                        // Set voice selected by the user.
                                        .withVoiceId(selectedVoice.getId())
                                        // Set format to MP3.
                                        .withOutputFormat(OutputFormat.Mp3);

                                Thread mLoadSoundFileThread2 = new Thread() {
                                    public void run() {
                                        SynthesizeSpeechResult ssr = mainActivity.client.synthesizeSpeech(synthesizeSpeechRequest);

                                        Runnable runnable = new Runnable() {
                                            public void run() {
                                                downMp4(ssr.getAudioStream(), textToRead, selectedVoice.getId());

                                            }
                                        };
                                        mHandler.post(runnable);

                                    }
                                };
                                mLoadSoundFileThread2.start();

                                // Create speech synthesis request.
                                SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =  new SynthesizeSpeechPresignRequest()
                                        // Set text to synthesize.
                                        .withText(textToRead)
                                        // Set voice selected by the user.
                                        .withVoiceId(selectedVoice.getId())
                                        // Set format to MP3.
                                        .withOutputFormat(OutputFormat.Mp3);


                                // Get the presigned URL for synthesized speech audio stream.
                                URL presignedSynthesizeSpeechUrl = mainActivity.client.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);
                                voiceUrl = presignedSynthesizeSpeechUrl.toString();

                            } else {
                                voiceUrl = HummingUtils.IMAGE_PATH +""+fileName;

                            }

                            // Create a media player to play the synthesized audio stream.
                            //if (mediaPlayer.isPlaying()) {
                                setupNewMediaPlayer();
                            //}
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                            try {
                                // Set media player's data source to previously obtained URL.
                                mediaPlayer.setDataSource(voiceUrl);

                            } catch (IOException e) {
                                Log.e("test","Unable to set data source for the media player! " + e.getMessage());
                            }

                            mediaPlayer.prepareAsync();

                        }
                    };
                    mLoadSoundFileThread.start();
                }
            }
        });*/
    }

    void setupNewMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                voiceIdx++;
                if(voiceIdx >= mainActivity.voices.size() -1){
                    voiceIdx = 0;
                }
                otherVoices.setText("" +(voiceIdx + 1) + "/" + mainActivity.voices.size());
                setupNewMediaPlayer();
                palyTfOtherVoice = true;
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }

    public void initPlayer(){

        // otherVoices
        voiceIdx = 0;
        otherVoices.setText("" +(voiceIdx + 1) + "/" + mainActivity.voices.size());
        // otherVoices List 랜덤 섞기
        long seed = System.nanoTime();
        Collections.shuffle(mainActivity.voices, new Random(seed));

    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);

        return (int)px;
    }

   /* private void showTextLayout(boolean showHide) {
        if(showHide){
            recordPlayLayout.setVisibility(View.INVISIBLE);
        }else{
            recordPlayLayout.setVisibility(View.VISIBLE);
           *//* LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recordPlayLayout.getLayoutParams();
            layoutParams.setMargins(0, (int) convertDpToPixel(5, getContext()), 0, 0);
            recordPlayLayout.setLayoutParams(layoutParams);*//*
        }
    }*/

    public void hidePanel() {
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        linearLayout.setVisibility(View.GONE);
    }

    public void showPanel() {
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        linearLayout.setVisibility(View.VISIBLE);
    }

    public void collapsePanel() {
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void setMiniPlayerAlphaProgress(View panel, float slideOffset) {
        float alpha = 1 - slideOffset;
        smallLinearView.setAlpha(alpha);
        if (alpha == 0) {
            smallLinearView.setVisibility(View.GONE);
            timeFrame.setVisibility(View.VISIBLE);
        } else {
            smallLinearView.setVisibility(View.VISIBLE);
            timeFrame.setVisibility(View.GONE);
        }
    }

    public void onPanelCollapsed(View panel) {
    }

    public void onPanelExpanded(View panel) {
        setDataTime(mainActivity.getVideoUrl());
    }

    public String getTagRepeatOne(){
        return repeatOne.getTag().toString();
    }

    public String getTagRepeat(){
        return repeat.getTag().toString();
    }

    public void floatRecordAction(){

        //customSTT.startCustomSTT();

        startNormalSTT("en-US");

        //String ss = "This is my test\nThis is your test\n80%";
        //setSpeakResult(ss);

    }

    /*public void showRecordPlayButton(int i, boolean nowRecord){
        Animation startAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_animation);
        if(i == 1){
            recordPlayButton1.setVisibility(View.VISIBLE);
            if(nowRecord){
                recordPlayButton1.startAnimation(startAnimation);
            }
        }else if(i == 2){

            recordPlayButton2.setVisibility(View.VISIBLE);
            if(nowRecord){
                recordPlayButton2.startAnimation(startAnimation);
            }
        }else if(i == 3){
            recordPlayButton3.setVisibility(View.VISIBLE);
            if(nowRecord){
                recordPlayButton3.startAnimation(startAnimation);
            }

        }
    }*/

    public void recordFileDelete(int i){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                + "/Voice Recorder/RECORDING_"
                + i
                + ".m4a");
        if(file.exists()){
            Log.e("test", "aaaaaaaaaaaaaaaaaaaaaa1Delete");
            file.delete();
        }
    }



    public void playRecordAudio(int i){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                + "/Voice Recorder/RECORDING_"
                + 1
                + ".amr");
        if(file.exists()){
            try {
                playAudio(file.getPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playSpeed(int speed) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainActivity.getVideoFragment().setSpeed(speed);
        }
    }

    public void showSpeakButton(boolean showFlg){
        //mainActivity.showSpeakButton(showFlg);
    }

    public void showRecordButton(boolean showFlg){
        HummingUtils.requestPermission(getActivity(), Manifest.permission.RECORD_AUDIO);
        HummingUtils.requestPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //mainActivity.showRecordButton(showFlg);
    }

    private int getColor(@ColorRes int colorResId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getContext().getResources().getColor(colorResId, null);
        } else return getContext().getResources().getColor(colorResId);
    }

    public void setDataTime(final String videoUrl) {

        totalDuration = mainActivity.getTotalDuration();
        otherVoices.setText("" +(voiceIdx + 1) + "/" + mainActivity.voices.size());

        finishOpeningSoundFile();
       /* Runnable runnable = new Runnable() {
            public void run() {

                downMp4(videoUrl);
                //TimeLineView.checkPrepare = true;
                //timeLineView.setVideo(videoUrl);
               // timeLineView.setBitmapUrl(timeLineView.getWidth(), mainActivity);

            }
        };
        mHandler.post(runnable);*/

    }

    private void playAudio(String url) throws Exception{
        killMediaPlayer();

        player = new MediaPlayer();
        player.setDataSource(url);
        player.prepare();
        player.start();
    }

    public void refreshPage(int page){

    }

    private void killMediaPlayer() {
        if(player != null){
            try {
                player.release();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static String StringReplace(String str){
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        str =str.replaceAll(match, " ");
        return str;
    }

    public void setSpeakResult(String speak){

        float rate = compareSentence(StringReplace(MainActivity.SEARCH_POPUP_VALUE), StringReplace(speak));
        /*SpannableString str = new SpannableString(StringReplace(speak));
        for (Integer[] mark: tmpString) {
            str.setSpan(new ForegroundColorSpan(Color.RED), mark[0], mark[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }*/

        StringBuffer sb = new StringBuffer();
        //sb.append(StringReplace(MainActivity.SEARCH_POPUP_VALUE));
        //sb.append("\n");
        sb.append(speak);
       // sb.append("\n");
        sb.append(" ("+((int)rate)+"%)");

        //mainActivity.addPlayListMenu(sb.toString());

    }

    public List<Datums> getPlayList(int nowPosition){
        RelationFragment fm = (RelationFragment)mAdapterViewPager.getFragment(0);
        List<Datums> datumList = fm.getPlayList(nowPosition);

        /*
        if(mainActivity.getVideoFragment().getNowPlayListType().equals("relation")){
            RelationFragment fm = (RelationFragment)mAdapterViewPager.getFragment(0);
            datumList = fm.getPlayList(nowPosition);
        }else{
            EpisodeFragment fm = (EpisodeFragment)mAdapterViewPager.getFragment(1);
            datumList = fm.getPlayList(nowPosition);
        }
        return null;*/
        return datumList;
    }

   /* public void playListPosition(int playPosition){
        List<Datums> datumList = getPlayList(playPosition);
        Datums datums = datumList.get(playPosition);
        mainActivity.setVideoEpisodeUrl(datums, playPosition);
    }*/

    public void setNextVideo(int nowPosition, boolean nextFlg) {
        String shuffleTag = shuffle.getTag().toString();
        boolean random = false;
        if (shuffleTag.equals("on")) {
            random = true;
        }
        List<Datums> datumList = getPlayList(nowPosition);
        if (random) {
            Random r = new Random();
            int n = datumList.size()-1;
            if (n < 01) {
                n = 0;
            }
            nowPosition = r.nextInt(n);
        } else {
            if (nextFlg) {
                nowPosition++;
            } else {
                nowPosition--;
            }
        }
        if (nowPosition > -1 && datumList.size() > nowPosition) {
            Datums datums = datumList.get(nowPosition);
            mainActivity.setVideoEpisodeUrl(datums, nowPosition, false);
        }
    }

    public float compareSentence(String str1Tmp, String str2Tmp){
        List<Integer[]> result = new ArrayList<>();
        str1Tmp = str1Tmp.trim().replaceAll("\\s{2,}", " ");
        str2Tmp = str2Tmp.trim().replaceAll("\\s{2,}", " ");
        StringBuffer sb1 = new StringBuffer();
        for (int i = 0; i < str1Tmp.length(); i++) {
            if(Pattern.matches("[a-zA-Z0-9\\s]", str1Tmp.charAt(i)+"")){
                sb1.append(str1Tmp.charAt(i));
            }
        }

        StringBuffer sb2 = new StringBuffer();
        for (int i = 0; i < str2Tmp.length(); i++) {
            if(Pattern.matches("[a-zA-Z0-9\\s]", str2Tmp.charAt(i)+"")){
                sb2.append(str2Tmp.charAt(i));
            }
        }

        String str1 = sb1.toString();
        String str2 = sb2.toString();

        Log.e("test", "***********" +str1);
        Log.e("test", "***********" +str2);


        String[] st1 = str1.split(" ");
        String[] st2 = str2.split(" ");
        StringBuffer resultStr = new StringBuffer();
        int idxj = 0;
        String tmp = "";
        for (int i = 0; i < st2.length; i++) {

            boolean check = false;
            for (int jj = idxj; jj < st1.length; jj++) {
                if (st1[jj].toLowerCase().equals(st2[i].toLowerCase())) {
                    check = true;
                    idxj = jj;
                    break;
                }
            }

            if(check) {
                resultStr.append(" ");
                resultStr.append(st2[i]);
            }else {
                Integer[] mark = new Integer[2];
                ;
                mark[0] = str2.indexOf(st2[i], StringReplace(tmp.toString().startsWith(" ") ? tmp.toString().substring(1) : tmp.toString()).length());
                mark[1] = mark[0] + st2[i].length();
                result.add(mark);
                resultStr.append(" -");
                resultStr.append(st2[i]);

            }
            tmp = tmp +" "+st2[i];
        }

        float oCnt = str1.split(" ").length;
        float sCnt = str2.split(" ").length;
        float rate = ((sCnt - result.size()) / oCnt) * 100;
        if(oCnt < sCnt){
            rate = rate - ((sCnt-oCnt) * 10);
        }
        if(rate < 0 ){
            rate = 0;
        }
        return rate;
    }

    private void startNormalSTT(String language) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, mainActivity.getVideoFragment().getAllText("allText"));
        intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        intent.putExtra("android.speech.extra.GET_AUDIO", true);

        startActivityForResult(intent, REQ_CODE_SPEECH);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE_SPEECH) {
            if(resultCode == RESULT_OK) {

                //mainActivity.hideVoiceBtn();
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String resultStr = result.get(0);
                setSpeakResult(resultStr);

                if(data != null){
                    mLoadSoundFileThread = new Thread() {
                        public void run() {
                            Bundle bundle = data.getExtras();
                            ArrayList<String> matches = bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
                            // the recording url is in getData:
                            Uri audioUri = data.getData();
                            ContentResolver contentResolver = getActivity().getContentResolver();
                            if(recodingCnt >= 3){
                                recodingCnt = 0;
                            }
                            mOutputFile = getOutputFile();
                            mOutputFile.getParentFile().mkdirs();
                            try {
                                InputStream filestream = contentResolver.openInputStream(audioUri);
                                //FileUtils.copyInputStreamToFile(filestream, mOutputFile);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    mLoadSoundFileThread.start();
                }
            }
        }
    }

    public void scrollHide(){
    }
    public void scrollShow(){
    }

    public void changeView(){
        mAdapterViewPager.notifyDataSetChanged();
    }

    public void changeView(int page){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                for (int i = 0; i < recodingCnt; i++) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                            + "/Voice Recorder/RECORDING_"
                            + i
                            + ".amr");
                    if(file.exists()){
                        file.delete();
                    }
                }
            }
        }, 100);

        Fragment fm = mAdapterViewPager.getFragment(page);
        fm.onResume();
//        mAdapterViewPager.getItemPosition(mAdapterViewPager.getFragment(page));
        //mainActivity.refreshFragment(page);
//        mAdapterViewPager.notifyDataSetChanged();
    }

    public void updateProgress(int pos){
        songProgress.setProgressWithAnim(pos);
        songProgress.setProgressWithAnim(0);
        seekbar.setProgress(pos);
        currentDur.setText(milliSecondsToTimer(pos));
    }

    public void maxProgress(int max){
        songProgress.cancelAnimation();
        songProgress.setMaxWithAnim(max);
        seekbar.setMax(max);

        totalDur.setText(milliSecondsToTimer(max));
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }


    @Override
    public void onDestroy() {
        if(customSTT != null) {
            customSTT.stopCustomSTT();
            customSTT = null;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                for (int i = 0; i < recodingCnt; i++) {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                            + "/Voice Recorder/RECORDING_"
                            + i
                            + ".amr");
                    if(file.exists()){
                        Log.e("test", "aaaaaaaaaaaaaaaaaaaaaa1Delete");
                        file.delete();
                    }
                }
            }
        }, 100);



        super.onDestroy();
    }

    /*private void startRecording() {
        if(recodingCnt >= 3){
            recodingCnt = 0;
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            mRecorder.setAudioEncodingBitRate(48000);
        } else {
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioEncodingBitRate(64000);
        }
        mRecorder.setAudioSamplingRate(16000);
        mOutputFile = getOutputFile();
        mOutputFile.getParentFile().mkdirs();
        mRecorder.setOutputFile(mOutputFile.getAbsolutePath());

        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartTime = SystemClock.elapsedRealtime();
            mHandler.postDelayed(mTickExecutor, 100);
            Log.d("Voice Recorder","started recording to "+mOutputFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e("Voice Recorder", "prepare() failed "+e.getMessage());
        }
    }

    protected  void stopRecording(boolean saveFile) {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        mStartTime = 0;
        mHandler.removeCallbacks(mTickExecutor);
        *//*if (!saveFile && mOutputFile != null) {
            mOutputFile.delete();
        }*//*
//        mainActivity.showRecordPlayButton(recodingCnt);
        boolean nowRecord = true;
        for (int i = recodingCnt; i > 0; i--) {
            File files = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                    + "/Voice Recorder/RECORDING_"
                    + i
                    + ".amr");
            if(files.exists()){
                showRecordPlayButton(i, nowRecord);
                nowRecord = false;
            }



        }
    }*/

    public void hideTab(){
        //appBarLayout.setExpanded(false, true);
    }

    public int getRecodingCnt(){
        return recodingCnt;
    }

    private File getOutputFile() {
        recodingCnt++;
        File files = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                + "/Voice Recorder/RECORDING_"
                + recodingCnt
                + ".amr");
        if(files.exists()){
            files.delete();
        }

        return new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                + "/Voice Recorder/RECORDING_"
                + recodingCnt
                + ".amr");
    }

    public void refreshRelation() {
        /*RelationFragment fm = (RelationFragment)mAdapterViewPager.getFragment(0);
        MainActivity.SEARCH_CHECK = true;
        fm.onResume();*/
    }

    public void showTab() {
        ////
        appBarLayout.setExpanded(true,true);
    }

    /*private void tick() {
        long time = (mStartTime < 0) ? 0 : (SystemClock.elapsedRealtime() - mStartTime);
        int minutes = (int) (time / 60000);
        int seconds = (int) (time / 1000) % 60;
        //int milliseconds = (int) (time / 100) % 10;
        //recordTime.setText(minutes+":"+(seconds < 10 ? "0"+seconds : seconds)+"."+milliseconds);
        recordTime.setText(minutes+":"+(seconds < 10 ? "0"+seconds : seconds));
        if (mRecorder != null) {
            amplitudes[iii] = mRecorder.getMaxAmplitude();
            //Log.d("Voice Recorder","amplitude: "+(amplitudes[i] * 100 / 32767));
            if (iii >= amplitudes.length -1) {
                iii = 0;
            } else {
                ++iii;
            }
        }
    }*/

}
