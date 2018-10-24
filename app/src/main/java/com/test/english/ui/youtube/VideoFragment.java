package com.test.english.ui.youtube;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.exam.english.R;
import com.github.pedrovgs.DraggablePanel;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.test.english.api.APIClient;
import com.test.english.api.APIInterface;
import com.test.english.api.Datums;
import com.test.english.api.PostResource;
import com.test.english.api.SearchResource;
import com.test.english.ui.main.MainActivity;
import com.test.english.util.HummingUtils;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressLint("ValidFragment")
public class VideoFragment extends Fragment implements VideoRendererEventListener {
    String TAG = "VideoFragment";

    float playSpeed = 1.0f;
    View mVideoLayout;
    private int cachedHeight;
    private DraggablePanel draggableView;
    private TextView alltext;
    private TextView alltextkr;
    private TextView speakko;

    int nowPlayCnt = 0;
    int nowPlayListNo = -1;
    String nowPlayListType = "relation";
    MainActivity mainActivity;

    String videoUrl = "";

    Context context;
    private static Handler songProgressHandler;

    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    MediaSource videoSource;
    DefaultDataSourceFactory dataSourceFactory;
    ExtractorsFactory extractorsFactory;
    View view;
    String videoIds = "";
    String youtubeid = "";
    boolean runProgress = true;

    ImageView favorite;
    private APIInterface apiInterface;

    private Handler mHandler2 = new Handler();
    public Thread mGoogleThread = null;

    String textJa = "";
    String textKo = "";
    ImageView downButton;
    Datums baseSentence;

    private Runnable textExecutor = new Runnable() {
        @Override
        public void run() {
            Handler handlers2 = new Handler();
            handlers2.postDelayed(new Runnable() {
                @Override public void run() {
                    alltextkr.setText(Html.fromHtml(textKo));
                    alltextkr.setVisibility(View.VISIBLE);
                }
            }, 100);
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        apiInterface = APIClient.getClient().create(APIInterface.class);

        view = inflater.inflate(R.layout.fragment_video, container, false);
        context = getActivity();
        mainActivity = (MainActivity) getActivity();

        songProgressHandler = new Handler(Looper.getMainLooper());
        //progressRunnable = new ProgressRunnable(mainActivity);

        initExoPlayer(view);

        alltext = (TextView) view.findViewById(R.id.alltext);
        alltextkr = (TextView) view.findViewById(R.id.alltextkr);
        speakko = (TextView) view.findViewById(R.id.speakko);

        mVideoLayout = view.findViewById(R.id.video_layout);
        setVideoAreaSize();

        return view;
    }

    /*public void checkFavorite(){
        String favoriteIds = mainActivity.checkFavorite(videoIds);
        if(!favoriteIds.equals("")){
            favorite.setTag("favorite_on");
            favorite.setImageResource(R.drawable.ic_action_favorited);
        }else{
            favorite.setTag("favorite_off");
            favorite.setImageResource(R.drawable.ic_action_favorite);
        }
    }*/

    public void putDataKo() {

        String textKo22 = textKo;
        String textJa22 = textJa;
        String speakkr22 = speakko.getText().toString();
        try {
            textKo22 = URLEncoder.encode(textKo22, "UTF-8");
            textJa22 = URLEncoder.encode(textJa22, "UTF-8");
            speakkr22 = URLEncoder.encode(speakkr22, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Call<PostResource> call = apiInterface.putSentenceKo(videoIds, textKo22, textJa22, speakkr22);
        call.enqueue(new Callback<PostResource>() {
            @Override
            public void onResponse(Call<PostResource> call, Response<PostResource> response) {
                Log.e("test", "==="+videoIds+" ");
            }
            @Override
            public void onFailure(Call<PostResource> call, Throwable t) {
                Log.d("test","================================================2"+t.getMessage());
                call.cancel();
            }
        });
    }

    public void putDataKoSpeakkr() {

        String textKo22 = "";
        String textJa22 = "";
        String speakkr22 = speakko.getText().toString();
        try {
            speakkr22 = URLEncoder.encode(speakkr22, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Call<PostResource> call = apiInterface.putSentenceKo(videoIds, textKo22, textJa22, speakkr22);
        call.enqueue(new Callback<PostResource>() {
            @Override
            public void onResponse(Call<PostResource> call, Response<PostResource> response) {
                Log.e("test", "==="+videoIds+" ");
            }
            @Override
            public void onFailure(Call<PostResource> call, Throwable t) {
                Log.d("test","================================================2"+t.getMessage());
                call.cancel();
            }
        });
    }

    public void getSpeakKo() {

        Call<SearchResource> call = apiInterface.getSpeak(alltext.getText().toString());
        call.enqueue(new Callback<SearchResource>() {
            @Override
            public void onResponse(Call<SearchResource> call, Response<SearchResource> response) {
                SearchResource resource = response.body();
                String ens = alltext.getText().toString().toLowerCase();
                if (resource != null && resource.hits != null) {
                    for (int i = 0; i < resource.hits.hits.size(); i++) {
                        String e = resource.hits.hits.get(i).source.get(HummingUtils.ElasticField.TEXT_EN).toString().toLowerCase();
                        if(ens.toLowerCase().contains(e)){
                            String k = resource.hits.hits.get(i).source.get(HummingUtils.ElasticField.SPEAK_KO).toString();
                            ens = ens.toLowerCase().replaceAll(e, k);
                        }
                    }
                }
                try {
                    speakko.setText(URLDecoder.decode(ens, "UTF-8"));
                    speakko.setVisibility(View.VISIBLE);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                putDataKoSpeakkr();
            }
            @Override
            public void onFailure(Call<SearchResource> call, Throwable t) {
                Log.d("test","================================================2"+t.getMessage());
                call.cancel();
            }
        });
    }

    public void initExoPlayer(View view){

        simpleExoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.player_view);
        simpleExoPlayerView.requestFocus();

        PlayerControlView controlView = simpleExoPlayerView.findViewById(R.id.exo_controller);
        /*ImageView youtubeButton = controlView.findViewById(R.id.youtubeButton);
        youtubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchYoutubeVideo(getActivity(), youtubeid);
            }
        });*/

        /*downButton = controlView.findViewById(R.id.small_video_down);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draggableView.minimize();
            }
        });

        ImageView share = controlView.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        favorite = controlView.findViewById(R.id.favorite);
        favorite.setTag("favorite_off");
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favorite.getTag().toString().equals("favorite_off")){
                    //postFavorite();
                }else if(favorite.getTag().toString().equals("favorite_on")){
                    //delete favorite
                    //putFavorite();
                }
            }
        });*/

        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
        //BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeterA);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //LoadControl loadControl = new DefaultLoadControl();

        //player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
        player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();
//        simpleExoPlayerView.hideController();

        simpleExoPlayerView.setPlayer(player);
        simpleExoPlayerView.setDefaultArtwork(null);

       // DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
        dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "exoplayer2example"), bandwidthMeterA);
        extractorsFactory = new DefaultExtractorsFactory();

        setMediaSource(Uri.parse(videoUrl));
        player.addListener(new ExoPlayer.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                Log.e(TAG, "Listener-onTimelineChanged..."+timeline.getPeriodCount()+" "+reason);
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.e(TAG, "Listener-onTracksChanged...");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.e(TAG, "Listener-onLoadingChanged...isLoading:"+isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.e(TAG, "Listener-onPlayerStateChanged..." + playbackState);
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    // media actually playing
                    simpleExoPlayerView.hideController();
                    PlaybackParameters params = new PlaybackParameters(playSpeed, 1.0f);
                    player.setPlaybackParameters(params);
                    int sss = (int)player.getDuration();
                    if(player.getDuration() > sss){
                        sss++;
                    }
                    //mainActivity.getVideoListFragment().maxProgress(sss);

                    Handler handlers2 = new Handler();
                    handlers2.postDelayed(new Runnable() {
                        @Override public void run() {
                            //finalProgress();
                        }
                    }, 100);
                } else if (playWhenReady) {
                    switch(playbackState) {
                        case Player.STATE_BUFFERING:
                            break;
                        case Player.STATE_ENDED:
                            if (draggableView.isMaximized()) {
                                simpleExoPlayerView.showController();
                            }
                            //removeProgress();
                            nowPlayCnt++;

                            if (draggableView.isMaximized()) {
                                // 한곡 반복
                                /*String repeat = mainActivity.getVideoListFragment().getTagRepeat();
                                if(repeat.equals("one")){
                                    startVideo();
                                } else {
                                    // 반복 횟수 - 하나의 곡에 대한 반복
                                    int repeatOne = Integer.valueOf(mainActivity.getVideoListFragment().getTagRepeatOne());
                                    if(nowPlayCnt < repeatOne){
                                        startVideo();
                                    }else{
                                        initNowPlayCnt();
                                        if(repeat.equals("all")){
                                            mainActivity.setNextVideo(nowPlayListNo);
                                        }else{
                                            buttonShow();
                                        }
                                    }
                                }*/
                            }
                            break;
                        case Player.STATE_IDLE:
                            break;
                        case Player.STATE_READY:
                            break;
                        default:
                            break;
                    }
                } else {
                    Handler handlers2 = new Handler();
                    handlers2.postDelayed(new Runnable() {
                        @Override public void run() {
                            //removeProgress();
                        }
                    }, 1000);
                    // player paused in any state
                }

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.e(TAG, "Listener-onRepeatModeChanged...");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.e(TAG, "Listener-onPositionDiscontinuity...");
            }

            @Override
            public void onSeekProcessed() {
                Log.e(TAG, "onSeekProcessed");
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e(TAG, "Listener-onPlayerError...");
                player.stop();
                //player.prepare(videoSource);
                //player.setPlayWhenReady(true);
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.e(TAG, "Listener-onPlaybackParametersChanged...");
            }
        });

        player.setPlayWhenReady(true); //run file/link when ready to play.
        player.setVideoDebugListener(this); //for listening to resolution change and  outputing the resolution

    }

    /*public void putFavorite() {

        String email = mainActivity.getEmailInfo();
        if(email.equals("")){
            mainActivity.openLoginPage();
        }else{
            favorite.setTag("favorite_off");
            favorite.setImageResource(R.drawable.ic_action_favorite);

            if(!videoIds.equals("")){
                Call<PostResource> call = apiInterface.putFavorite(email, videoIds);
                call.enqueue(new Callback<PostResource>() {
                    @Override
                    public void onResponse(Call<PostResource> call, Response<PostResource> response) {
                        mainActivity.refreshFavorite();
                    }

                    @Override
                    public void onFailure(Call<PostResource> call, Throwable t) {
                        Log.d("test","================================================2"+t.getMessage());
                        call.cancel();
                    }
                });

            }
        }
    }

    public void postFavorite() {

        String email = mainActivity.getEmailInfo();
        if(!email.equals("")){

            favorite.setTag("favorite_on");
            favorite.setImageResource(R.drawable.ic_action_favorited);
            //insert favorite

            Call<PostResource> call = apiInterface.postFavorite(email, videoIds);
            call.enqueue(new Callback<PostResource>() {
                @Override
                public void onResponse(Call<PostResource> call, Response<PostResource> response) {
                    mainActivity.refreshFavorite();

                }

                @Override
                public void onFailure(Call<PostResource> call, Throwable t) {
                    Log.d("test","================================================2"+t.getMessage());
                    call.cancel();
                }
            });
        } else {
            mainActivity.openLoginPage();
        }
    }*/

    public void setMediaSource(Uri mp4VideoUri){
        videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);
        player.prepare(videoSource);
    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {
    }


    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
    }

    @Override
    public void onVideoInputFormatChanged(Format format) {
    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.v(TAG, "onVideoSizeChanged ["  + " width: " + width + " height: " + height + "]");
        //resolutionTextView.setText("RES:(WxH):"+width+"X"+height +"\n           "+height+"p");
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {
    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {
    }

    public void initNowPlayCnt(){
        nowPlayCnt = 0;
    }

    public void setSpeed(int speed){
        BigDecimal pSpeed = new BigDecimal(speed);
        pSpeed = pSpeed.divide(new BigDecimal(100),1,BigDecimal.ROUND_UP);
        playSpeed = pSpeed.floatValue();
        setUrl(videoUrl, videoIds, youtubeid);
    }

    public int getTotalDuration(){
        return (int)player.getDuration();
    }

    public void seekTo(int seek){
        player.seekTo(seek);
    }

    public void seekToCurrent(int seek){
        player.seekTo(player.getCurrentPosition()+seek);
    }

    public int getCurrentPosition(){
        return (int)player.getCurrentPosition();
    }

    public String getVideoUrl(){
        return videoUrl;
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

    public String getAllText(String type){
        if(type.equals("allText")){
            return alltext.getText().toString();
        }else if(type.equals("allTextKr")){
            return alltextkr.getText().toString();
        }
        return alltext.getText().toString();
    }

    public void showText(){
        Handler handlers = new Handler();
        handlers.postDelayed(new Runnable() {
            @Override public void run() {
                alltext.setVisibility(View.VISIBLE);
                alltextkr.setVisibility(View.VISIBLE);
                speakko.setVisibility(View.VISIBLE);
            }
        }, 0);

    }
    public void showHideText(){
        if(alltext.getVisibility() != View.VISIBLE){
            alltext.setVisibility(View.VISIBLE);
        }else{
            alltext.setVisibility(View.GONE);
        }
    }

    public void showHideTextKr(){
        if(alltextkr.getVisibility() != View.VISIBLE){
            alltextkr.setVisibility(View.VISIBLE);
        }else{
            alltextkr.setVisibility(View.GONE);
        }
    }

    public void showHideTextSpeak(){
        if(speakko.getVisibility() != View.VISIBLE){
            speakko.setVisibility(View.VISIBLE);
        }else{
            speakko.setVisibility(View.GONE);
        }
    }

    public String getPopupText(){
        return alltext.getText().toString();
    }

    public void onMinimized(){
        buttonHide();
        textHide();
        //removeProgress();
    }

    public void onStartTouch(){
    }

    public void onMaximized(){
        buttonShow();
        showText();
    }

    public void onResumeFragment(){
    }

    public void onPauseFragment(){
        //removeProgress();
    }

    private void setVideoAreaSize() {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mVideoLayout.getWidth();
                cachedHeight = (int) (width * 405f / 720f);
//                cachedHeight = (int) (width * 3f / 4f);
//                cachedHeight = (int) (width * 9f / 16f);
                ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = cachedHeight;
                mVideoLayout.setLayoutParams(videoLayoutParams);

            }
        });
    }

    public void setUrl(String url, String ids, String youtubeidParam) {

        if (HummingUtils.getExtension(url).equals("mp3") || url.contains("OutputFormat=mp3")) {
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.vinty1);
            simpleExoPlayerView.setDefaultArtwork(largeIcon);
        } else {
            simpleExoPlayerView.setDefaultArtwork(null);
        }

        videoIds = ids;
        videoUrl = url;
        youtubeid = youtubeidParam;

        if (player == null) {
            initExoPlayer(view);
        }

        Handler handlers = new Handler();
        /*handlers.postDelayed(new Runnable() {
            @Override public void run() {
               checkFavorite();
            }
        }, 100);*/

        setMediaSource(Uri.parse(url));
        //player.setVideoURI(Uri.parse(url));
        initNowPlayCnt();
        startVideo();
    }


    public void startVideo() {
        if(player.getContentPosition() >= player.getDuration()){
            player.seekTo(0);
        }
        player.setPlayWhenReady(true);

        Handler handlers2 = new Handler();
        handlers2.postDelayed(new Runnable() {
            @Override public void run() {
                postPlay();
            }
        }, 100);

        //player.start();
        //player.requestFocus();
    }

    public void postPlay(){
        Call<PostResource> call = apiInterface.postPlay(videoIds);
        call.enqueue(new Callback<PostResource>() {
            @Override
            public void onResponse(Call<PostResource> call, Response<PostResource> response) {
                Log.e("test", "==="+videoIds+" ");
            }

            @Override
            public void onFailure(Call<PostResource> call, Throwable t) {
                Log.d("test","================================================2"+t.getMessage());
                call.cancel();
            }
        });
    }

    public void startVideoIfStop() {
        //if (!player.isPlaying()) {
            startVideo();
        //}
    }

    public void buttonHide(){
        if(downButton != null){
            downButton.setVisibility(View.INVISIBLE);
        }

        //youtubeButton.setVisibility(View.INVISIBLE);
    }

    public void buttonShow(){

        if(downButton != null){
            downButton.setVisibility(View.VISIBLE);
        }
       /* if (!videoViewMedia.isPlaying() && videoViewMedia.getVisibility() == View.VISIBLE) {
            youtubeButton.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
        }*/

    }

    public void textHide(){
        Handler handlers = new Handler();
        handlers.postDelayed(new Runnable() {
            @Override public void run() {
                alltext.setVisibility(View.GONE);
                alltextkr.setVisibility(View.GONE);
                speakko.setVisibility(View.GONE);
            }
        }, 0);

    }

    public void pauseVideo() {
        //videoViewMedia.pause();
    }

    public void setPlaySpeed(float playSpeed) {
        this.playSpeed = playSpeed;
    }

    public void setAlltext(String alltextParam, String alltextKrParam,  String speakkoParam){
        try {
            alltextkr.setText(Html.fromHtml(URLDecoder.decode(alltextKrParam, "UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        alltext.setText(alltextParam);
        speakko.setText(speakkoParam);

        try {
            speakko.setText(URLDecoder.decode(speakkoParam, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (alltextParam.trim().equals("I'm out of here.")) {
            Handler handler11 = new Handler();
            handler11.postDelayed(new Runnable() {
                @Override public void run() {
                    speakko.setText("아 마러 히r");//I'm outta here.
                    alltextkr.setText("난 이만 가 볼게");
                }
            }, 200);
        } else {
            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override public void run() {
                    if(alltextkr.getText().toString().equals("")) {
                        mGoogleThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //textJa = HummingUtils.translateText(alltext.getText().toString(), "en", "ja");
                                //textKo = HummingUtils.translateText(textJa, "ja", "ko");
                                mHandler2.postDelayed(textExecutor, 100);
                                putDataKo();
                            }
                        });
                        mGoogleThread.start();
                    }
                }
            }, 200);
            Handler handler11 = new Handler();
            handler11.postDelayed(new Runnable() {
                @Override public void run() {
                    if(speakko.getText().toString().equals("")) {
                        getSpeakKo();
                    }
                }
            }, 200);
        }
    }

    public void setNowPlayListNo(int nowPlayListNo){
        this.nowPlayListNo = nowPlayListNo;
    }

    public int getNowPlayListNo(){
        return nowPlayListNo;
    }

    /*private void finalProgress() {
        runProgress = true;
        songProgressHandler.post(progressRunnable);
    }


    private void removeProgress() {
        runProgress = false;
        songProgressHandler.removeCallbacks(progressRunnable);
        songProgressHandler.removeCallbacksAndMessages(null);
    }

    public void updateProgress() {
            int pos = (int)player.getCurrentPosition();
            mainActivity.getVideoListFragment().updateProgress(pos);
            if(runProgress && player.getDuration() > pos){
                songProgressHandler.postDelayed(progressRunnable, 100);
            }
    }*/

    /*public void setSentence(String sentence) {
        if(palyTfOtherVoice){
            palyTfOtherVoice = false;
            Voice selectedVoice = (Voice) mainActivity.voices.get(0);

            SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =  new SynthesizeSpeechPresignRequest()
                    .withText(sentence)
                    .withVoiceId(selectedVoice.getId())
                    .withOutputFormat(OutputFormat.Mp3);

            URL presignedSynthesizeSpeechUrl = mainActivity.client.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);

            setUrl2(presignedSynthesizeSpeechUrl.toString(), "");

            palyTfOtherVoice = true;
        }
    }*/

    boolean palyTfOtherVoice = true;
    public void setBaseSentence(Datums baseSentence) {
        this.baseSentence = baseSentence;
    }

    public Datums getBaseSentence(){
        return baseSentence;
    }
    /*private static class ProgressRunnable implements Runnable {

        private final WeakReference<MainActivity> activityWeakReference;

        public ProgressRunnable(MainActivity myClassInstance) {
            activityWeakReference = new WeakReference<MainActivity>(myClassInstance);
        }

        @Override
        public void run() {
            MainActivity mainActivity = activityWeakReference.get();
            mainActivity.getVideoFragment().updateProgress();
        }
    }*/
    public void setView(DraggablePanel draggablePanel) {
        this.draggableView =  draggablePanel;
    }
	
	
}
