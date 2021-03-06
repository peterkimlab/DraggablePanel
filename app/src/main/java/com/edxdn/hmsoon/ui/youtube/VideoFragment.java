package com.edxdn.hmsoon.ui.youtube;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.edxdn.hmsoon.R;
import com.edxdn.hmsoon.application.MyCustomApplication;
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
import com.edxdn.hmsoon.api.APIClient;
import com.edxdn.hmsoon.api.APIInterface;
import com.edxdn.hmsoon.api.Datums;
import com.edxdn.hmsoon.api.PostResource;
import com.edxdn.hmsoon.ui.main.MainActivity;
import com.edxdn.hmsoon.util.HummingUtils;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class VideoFragment extends Fragment implements VideoRendererEventListener {
    String TAG = "VideoFragment";

    private float playSpeed = 1.0f;
    private View mVideoLayout;
    private int cachedHeight;
    private DraggablePanel draggableView;
    private int nowPlayCnt = 0;
    private int nowPlayListNo = -1;
    private String nowPlayListType = "relation";
    private MainActivity mainActivity;

    private String videoUrl = "";

    private Context context;
    private ProgressRunnable progressRunnable;
    private static Handler songProgressHandler;

    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer mSimpleExoPlayer;
    private MediaSource videoSource;
    private DefaultDataSourceFactory dataSourceFactory;
    private ExtractorsFactory extractorsFactory;
    private View view;
    private String videoIds = "";
    private String youtubeid = "";
    boolean runProgress = true;
    private ImageView favorite;
    private APIInterface apiInterface;
    private Handler mHandler2 = new Handler();
    ImageView downButton;
    Datums baseSentence;

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
        progressRunnable = new ProgressRunnable(mainActivity);

        initExoPlayer(view);

        mVideoLayout = view.findViewById(R.id.video_layout);
        setVideoAreaSize();

        return view;
    }

    public void checkFavorite() {
        String favoriteIds = mainActivity.checkFavorite(videoIds);
        if (!favoriteIds.equals("")) {
            favorite.setTag("favorite_on");
            favorite.setImageResource(R.drawable.ic_action_favorited);
        } else {
            favorite.setTag("favorite_off");
            favorite.setImageResource(R.drawable.ic_action_favorite);
        }
    }

    public void initExoPlayer(View view) {

        simpleExoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.player_view);
        simpleExoPlayerView.requestFocus();

        PlayerControlView controlView = simpleExoPlayerView.findViewById(R.id.exo_controller);

        ImageView youtubeButton = controlView.findViewById(R.id.youtubeButton);
        youtubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchYoutubeVideo(getActivity(), youtubeid);
            }
        });

        downButton = controlView.findViewById(R.id.small_video_down);
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
                if (favorite.getTag().toString().equals("favorite_off")) {
                    postFavorite();
                } else if (favorite.getTag().toString().equals("favorite_on")) {
                    putFavorite();
                }
            }
        });

        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
        //BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeterA);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //LoadControl loadControl = new DefaultLoadControl();

        //player = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();

        simpleExoPlayerView.setPlayer(mSimpleExoPlayer);
        simpleExoPlayerView.setDefaultArtwork(null);

       // DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
        dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), "exoplayer2example"), bandwidthMeterA);
        extractorsFactory = new DefaultExtractorsFactory();

        setMediaSource(Uri.parse(videoUrl));
        mSimpleExoPlayer.addListener(new ExoPlayer.EventListener() {

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
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    // media actually playing
                    simpleExoPlayerView.hideController();
                    PlaybackParameters params = new PlaybackParameters(playSpeed, 1.0f);
                    mSimpleExoPlayer.setPlaybackParameters(params);
                    int duration = (int) mSimpleExoPlayer.getDuration();
                    if (mSimpleExoPlayer.getDuration() > duration) {
                        duration++;
                    }
                    mainActivity.getVideoListFragment().maxProgress(duration);

                    Handler handlers2 = new Handler();
                    handlers2.postDelayed(new Runnable() {
                        @Override public void run() {
                            finalProgress();
                        }
                    }, 100);
                } else if (playWhenReady) {
                    switch (playbackState) {
                        case Player.STATE_BUFFERING:
                            break;
                        case Player.STATE_ENDED:
                            if (draggableView.isMaximized()) {
                                simpleExoPlayerView.showController();
                            }
                            removeProgress();
                            nowPlayCnt++;
                            if (draggableView.isMaximized()) {
                                // 한곡 반복
                                String repeat = mainActivity.getVideoListFragment().getTagRepeat();
                                if (repeat.equals("one")) {
                                    startVideo();
                                } else {
                                    // 반복 횟수 - 하나의 곡에 대한 반복
                                    int repeatOne = Integer.valueOf(mainActivity.getVideoListFragment().getTagRepeatOne());
                                    if (nowPlayCnt < repeatOne) {
                                        startVideo();
                                    } else {
                                        initNowPlayCnt();
                                        if (repeat.equals("all")) {
                                            mainActivity.setNextVideo(nowPlayListNo);
                                        } else {
                                            buttonShow();
                                        }
                                    }
                                }
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
                mSimpleExoPlayer.stop();
                //player.prepare(videoSource);
                //player.setPlayWhenReady(true);
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.e(TAG, "Listener-onPlaybackParametersChanged...");
            }
        });

        mSimpleExoPlayer.setPlayWhenReady(true); //run file/link when ready to play.
        mSimpleExoPlayer.setVideoDebugListener(this); //for listening to resolution change and  outputing the resolution

    }

    public void putFavorite() {

        String email = mainActivity.getEmailInfo();
        if (email.equals("")) {
            //mainActivity.openLoginPage();
        } else {
            favorite.setTag("favorite_off");
            favorite.setImageResource(R.drawable.ic_action_favorite);

            if(!videoIds.equals("")){
                Call<PostResource> call = apiInterface.putFavorite(email, videoIds);
                call.enqueue(new Callback<PostResource>() {
                    @Override
                    public void onResponse(Call<PostResource> call, Response<PostResource> response) {
                        //mainActivity.refreshFavorite();
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
        if (!email.equals("")) {

            favorite.setTag("favorite_on");
            favorite.setImageResource(R.drawable.ic_action_favorited);
            //insert favorite

            Call<PostResource> call = apiInterface.postFavorite(email, videoIds);
            call.enqueue(new Callback<PostResource>() {
                @Override
                public void onResponse(Call<PostResource> call, Response<PostResource> response) {
                    //mainActivity.refreshFavorite();

                }

                @Override
                public void onFailure(Call<PostResource> call, Throwable t) {
                    Log.d("test","================================================2"+t.getMessage());
                    call.cancel();
                }
            });
        } else {
            //mainActivity.openLoginPage();
        }
    }

    public void setMediaSource(Uri mp4VideoUri){
        videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);
        mSimpleExoPlayer.prepare(videoSource);
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
        return (int)mSimpleExoPlayer.getDuration();
    }

    public void seekTo(int seek){
        mSimpleExoPlayer.seekTo(seek);
    }

    public void seekToCurrent(int seek){
        mSimpleExoPlayer.seekTo(mSimpleExoPlayer.getCurrentPosition()+seek);
    }

    public int getCurrentPosition(){
        return (int)mSimpleExoPlayer.getCurrentPosition();
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

    public void onMinimized() {
        buttonHide();
        //textHide();
        //removeProgress();
    }

    public void onStartTouch() {
    }

    public void onMaximized() {
        buttonShow();
        //showText();
    }

    public void onResumeFragment() {
    }

    public void onPauseFragment() {
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

        if (mSimpleExoPlayer == null) {
            initExoPlayer(view);
        }

        Handler handlers = new Handler();
        handlers.postDelayed(new Runnable() {
            @Override public void run() {
               checkFavorite();
            }
        }, 100);

        setMediaSource(Uri.parse(url));
        //player.setVideoURI(Uri.parse(url));
        setWatchedVideo();
        initNowPlayCnt();
        startVideo();
    }

    private void setWatchedVideo() {

        String email = MyCustomApplication.getMainInstance().getEmailInfo();

        Call<PostResource> call = apiInterface.postWatched(email, videoIds);
        call.enqueue(new Callback<PostResource>() {
            @Override
            public void onResponse(Call<PostResource> call, Response<PostResource> response) {
                Log.e("watched video id ", " :: " + videoIds + " ");
            }

            @Override
            public void onFailure(Call<PostResource> call, Throwable t) {
                call.cancel();
            }
        });

    }


    public void startVideo() {
        if (mSimpleExoPlayer.getContentPosition() >= mSimpleExoPlayer.getDuration()) {
            mSimpleExoPlayer.seekTo(0);
        }
        mSimpleExoPlayer.setPlayWhenReady(true);

        Handler handlers2 = new Handler();
        handlers2.postDelayed(new Runnable() {
            @Override public void run() {
                postPlay();
            }
        }, 100);

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

    public void pauseVideo() {
        //videoViewMedia.pause();
    }

    public void setPlaySpeed(float playSpeed) {
        this.playSpeed = playSpeed;
    }



    public void setNowPlayListNo(int nowPlayListNo){
        this.nowPlayListNo = nowPlayListNo;
    }

    public int getNowPlayListNo() {
        return nowPlayListNo;
    }

    private void finalProgress() {
        runProgress = true;
        songProgressHandler.post(progressRunnable);
    }


    private void removeProgress() {
        runProgress = false;
        songProgressHandler.removeCallbacks(progressRunnable);
        songProgressHandler.removeCallbacksAndMessages(null);
    }

    public void updateProgress() {
            int pos = (int) mSimpleExoPlayer.getCurrentPosition();
            mainActivity.getVideoListFragment().updateProgress(pos);
            if(runProgress && mSimpleExoPlayer.getDuration() > pos){
                songProgressHandler.postDelayed(progressRunnable, 100);
            }
    }

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

    private static class ProgressRunnable implements Runnable {

        private final WeakReference<MainActivity> activityWeakReference;

        public ProgressRunnable(MainActivity myClassInstance) {
            activityWeakReference = new WeakReference<MainActivity>(myClassInstance);
        }

        @Override
        public void run() {
            MainActivity mainActivity = activityWeakReference.get();
            mainActivity.getVideoFragment().updateProgress();
        }
    }
    public void setView(DraggablePanel draggablePanel) {
        this.draggableView =  draggablePanel;
    }

}
