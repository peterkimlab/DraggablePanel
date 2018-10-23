/*
 * MIT License
 *
 * Copyright (c) 2016 Knowledge, education for life.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.test.english.video;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.View;
import com.test.english.util.HummingUtils;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import life.knowledge4.videotrimmer.utils.BackgroundExecutor;
import life.knowledge4.videotrimmer.utils.UiThreadExecutor;

public class TimeLineViewMini extends View {

    private Uri mVideoUri;
    private String mVideoUrl;
    private int mHeightView;
    private LongSparseArray<Bitmap> mBitmapList = null;
    private Long[] seekArry;
    public static boolean checkPrepare = false;
    Context con;
    int idxx = 0;
    List<String> thumbnails;

    public TimeLineViewMini(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLineViewMini(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mHeightView = getContext().getResources().getDimensionPixelOffset(life.knowledge4.videotrimmer.R.dimen.frames_video_height_mini);
    }

    public static float convertDpToPixel(float dp, Context context){

        Resources resources = context.getResources();

        DisplayMetrics metrics = resources.getDisplayMetrics();

        float px = dp * (metrics.densityDpi / 160f);

        return px;

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minW, widthMeasureSpec, 1);

        final int minH = getPaddingBottom() + getPaddingTop() + mHeightView;
        int h = resolveSizeAndState(minH, heightMeasureSpec, 1);

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onSizeChanged(final int w, int h, final int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        if (w != oldW) {
            Log.d("test", "AA2");
            if (checkPrepare) {
                getBitmap(w);
            }

        }
    }
    public void setBitmap(int ww){

    }

    public void setBitmap(int ww, Context con){
        final int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();

        int w = resolveSizeAndState(minW, getMeasuredWidth(), 1);

        this.con = con;
        getBitmap(ww);
    }

    public void getBitmap(final int viewWidth) {

        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
                                       @Override
                                       public void execute() {
                                           try {
                                               Log.e("test", "-------=============");
                                               LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();

                                               MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                                               if (Build.VERSION.SDK_INT >= 14)
                                                   mediaMetadataRetriever.setDataSource(mVideoUrl, new HashMap<String, String>());
                                               else
                                                   mediaMetadataRetriever.setDataSource(mVideoUrl);

                                               // Retrieve media data
                                               long videoLengthInMs = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;

                                               // Set thumbnail properties (Thumbs are squares)
                                               final int thumbWidth = mHeightView;
                                               final int thumbHeight = mHeightView;
                                               int numThumbs = (int) Math.ceil(((float) viewWidth) / thumbWidth);
                                               final long interval = videoLengthInMs / numThumbs;

                                               seekArry = new Long[numThumbs];

                                               if(thumbnails != null){ //5
                                                   List<Bitmap> list = new ArrayList<>();

                                                   for (int i = 0; i < thumbnails.size(); i++) {
                                                       URL url = new URL(HummingUtils.IMAGE_PATH+thumbnails.get(i).toString());
                                                       Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                                       bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
                                                       list.add(bitmap);
                                                   }
                                                   BigDecimal la = new BigDecimal(list.size());
                                                   BigDecimal nu = new BigDecimal(numThumbs);
                                                   BigDecimal value = la.divide(nu, 1, BigDecimal.ROUND_UP);
                                                   for (int i = 0; i < numThumbs; i++) { //10
                                                       int idx = value.multiply(new BigDecimal(i)).intValue();
                                                       if(idx >= list.size()){
                                                           idx = idx-1;
                                                       }
                                                       try {
                                                           thumbnailList.put(i, list.get(idx));
                                                           seekArry[i] = i * interval;
                                                       } catch (Exception e) {
                                                           e.printStackTrace();
                                                       }

                                                   }
                                               }else{

                                                   for (int i = 0; i < numThumbs; ++i) {

                                                       Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(i * interval, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                                                       try {
                                                           bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
                                                       } catch (Exception e) {
                                                           e.printStackTrace();
                                                       }
                                                       thumbnailList.put(i, bitmap);
                                                       seekArry[i] = i * interval;
                                                   }
                                               }

                                               mediaMetadataRetriever.release();
                                               returnBitmaps(thumbnailList);
                                           } catch (final Throwable e) {
                                               Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                                           }
                                       }
                                   }
        );

       /* Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                final int thumbWidth = mHeightView;
                final int thumbHeight = mHeightView;
                LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();
                for (int i =0; i < seekArry.length;i++){
                    idxx = i;
                    GlideApp.with(con)
                            .asBitmap()
                            .load(mVideoUri)
  *//*                          .override(100,
                                    100)*//*
                            .transform(new GlideThumbnailTransformation(seekArry[i]))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    Bitmap bitmap = Bitmap.createScaledBitmap(resource, thumbWidth, thumbHeight, false);
                                    thumbnailList.put(idxx, resource);
                                }
                            });
                }
                returnBitmaps(thumbnailList);
            }
        }, 3000);*/
    }

    public void setBitmapUrl(int ww, Context con){
        final int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();

        int w = resolveSizeAndState(minW, getMeasuredWidth(), 1);


        getBitmapUrlGlide(ww, con);
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);

            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    private void getBitmapUrlGlide2(final int viewWidth, final Context con) {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
                                       @Override
                                       public void execute() {
                                           try {
                                               LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();

                                               mVideoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
                                              /* FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                                               mmr.setDataSource(mVideoUrl);
                                               mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
                                               mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
                                               Bitmap b = mmr.getFrameAtTime(2000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
                                               byte [] artwork = mmr.getEmbeddedPicture();
                                               mmr.release();*/

                                               MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

                                               if (Build.VERSION.SDK_INT >= 14)
                                                   mediaMetadataRetriever.setDataSource(mVideoUrl, new HashMap<String, String>());
                                               else
                                                   mediaMetadataRetriever.setDataSource(mVideoUrl);

                                               // Retrieve media data
                                               long videoLengthInMs = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                                               // Set thumbnail properties (Thumbs are squares)
                                               final int thumbWidth = mHeightView;
                                               final int thumbHeight = mHeightView;
                                               int numThumbs = (int) Math.ceil(((float) viewWidth) / thumbWidth);
                                               final long interval = videoLengthInMs / numThumbs;

                                               seekArry = new Long[numThumbs];

                                               // FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                                             /*  if (Build.VERSION.SDK_INT >= 14)
                                                   mmr.setDataSource(mVideoUrl, new HashMap<String, String>());
                                               else
                                                   mmr.setDataSource(mVideoUrl);*/

                                               //mmr.setDataSource("https://www.rmp-streaming.com/media/bbb-360p.mp4");
                                               //mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
                                               //mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
                                               mVideoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
                                               mediaMetadataRetriever.release();
                                               Log.e("test",mVideoUrl);
                                               Uri uri =  Uri.parse( mVideoUrl);

                                              /* FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
                                               retriever.setDataSource(con ,uri); // linkToVideo is  Uri

                                               for (int i = 0; i < numThumbs; ++i) {
                                                   Bitmap bitmap = retriever.getFrameAtTime(i * interval * 1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
                                                   //byte [] artwork = mmr.getEmbeddedPicture();

                                                   try {
                                                       bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
                                                   } catch (Exception e) {
                                                       e.printStackTrace();
                                                   }
                                                   thumbnailList.put(i, bitmap);
                                                   seekArry[i] = i * interval;
                                               }

                                               retriever.release();*/


                                               returnBitmaps(thumbnailList);
                                           } catch (final Throwable e) {
                                               Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                                           }
                                       }
                                   }
        );
    }

    private void getBitmapUrlGlide(final int viewWidth, final Context con) {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
                                       @Override
                                       public void execute() {
                                           try {
                                               LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();


                                               /*AWSCredentials credentials = new BasicAWSCredentials("AKIAI4AOE466NMHECQDQ", "0TZwUpxpkgmtQ8f6lxkUU5uONJmdAP+l3etx5xOJ");

                                               AmazonS3 s3client = new AmazonS3Client(credentials);
                                               URL url = null;
                                               try {
                                                   System.out.println("Generating pre-signed URL.");
                                                   java.util.Date expiration = new java.util.Date();
                                                   long milliSeconds = expiration.getTime();
                                                   milliSeconds += 1000 * 60 * 60; // Add 1 hour.
                                                   expiration.setTime(milliSeconds);

                                                   GeneratePresignedUrlRequest generatePresignedUrlRequest =
                                                           new GeneratePresignedUrlRequest("my.test0002", "f2.mp4");
                                                   generatePresignedUrlRequest.setMethod(HttpMethod.GET);
                                                   generatePresignedUrlRequest.setExpiration(expiration);

                                                   url = s3client.generatePresignedUrl(generatePresignedUrlRequest);

                                                   System.out.println("Pre-Signed URL = " + url.toString());
                                               } catch (AmazonServiceException exception) {
                                                   System.out.println("Caught an AmazonServiceException, " +
                                                           "which means your request made it " +
                                                           "to Amazon S3, but was rejected with an error response " +
                                                           "for some reason.");
                                                   System.out.println("Error Message: " + exception.getMessage());
                                                   System.out.println("HTTP  Code: "    + exception.getStatusCode());
                                                   System.out.println("AWS Error Code:" + exception.getErrorCode());
                                                   System.out.println("Error Type:    " + exception.getErrorType());
                                                   System.out.println("Request ID:    " + exception.getRequestId());
                                               } catch (AmazonClientException ace) {
                                                   System.out.println("Caught an AmazonClientException, " +
                                                           "which means the client encountered " +
                                                           "an internal error while trying to communicate" +
                                                           " with S3, " +
                                                           "such as not being able to access the network.");
                                                   System.out.println("Error Message: " + ace.getMessage());
                                               }*/

                                               MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

                                               if (Build.VERSION.SDK_INT >= 14)
                                                   mediaMetadataRetriever.setDataSource(mVideoUrl, new HashMap<String, String>());
                                               else
                                                   mediaMetadataRetriever.setDataSource(mVideoUrl);

                                               // Retrieve media data
                                               long videoLengthInMs = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                                               // Set thumbnail properties (Thumbs are squares)
                                               final int thumbWidth = mHeightView;
                                               final int thumbHeight = mHeightView;
                                               int numThumbs = (int) Math.ceil(((float) viewWidth) / thumbWidth);
                                               final long interval = videoLengthInMs / numThumbs;

                                               seekArry = new Long[numThumbs];




                                               for (int i = 0; i < numThumbs; ++i) {

                                                   Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(i * interval, MediaMetadataRetriever.OPTION_CLOSEST);
                                                   try {
                                                       bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
                                                   } catch (Exception e) {
                                                       e.printStackTrace();
                                                   }
                                                   thumbnailList.put(i, bitmap);
                                                   seekArry[i] = i * interval;
                                               }

                                               mediaMetadataRetriever.release();
                                               returnBitmaps(thumbnailList);
                                           } catch (final Throwable e) {
                                               Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                                           }
                                       }
                                   }
        );
    }
  /*  public void loadPreview(long currentPosition, long max) {
        GlideApp.with(imageView)
                .load(thumbnailsUrl)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .transform(new GlideThumbnailTransformation(currentPosition))
                .into(imageView);
    }*/
    private void getBitmapUrl(final int viewWidth) {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
                                       @Override
                                       public void execute() {
                                           try {
                                               LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();

                                               MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                                               if (Build.VERSION.SDK_INT >= 14)
                                                   mediaMetadataRetriever.setDataSource(mVideoUrl, new HashMap<String, String>());
                                               else
                                                   mediaMetadataRetriever.setDataSource(mVideoUrl);

                                               // Retrieve media data
                                               long videoLengthInMs = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;

                                               // Set thumbnail properties (Thumbs are squares)
                                               final int thumbWidth = mHeightView;
                                               final int thumbHeight = mHeightView;
                                               int numThumbs = (int) Math.ceil(((float) viewWidth) / thumbWidth);
                                               final long interval = videoLengthInMs / numThumbs;

                                               seekArry = new Long[numThumbs];
                                               for (int i = 0; i < numThumbs; ++i) {

                                                   Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(i * interval, MediaMetadataRetriever.OPTION_CLOSEST);
                                                   try {
                                                       bitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, false);
                                                   } catch (Exception e) {
                                                       e.printStackTrace();
                                                   }
                                                   thumbnailList.put(i, bitmap);
                                                   seekArry[i] = i * interval;
                                               }

                                               mediaMetadataRetriever.release();
                                               returnBitmaps(thumbnailList);
                                           } catch (final Throwable e) {
                                               Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                                           }
                                       }
                                   }
        );
    }

    private void returnBitmaps(final LongSparseArray<Bitmap> thumbnailList) {





        UiThreadExecutor.runTask("", new Runnable() {
                    @Override
                    public void run() {
                        mBitmapList = thumbnailList;
                        invalidate();
                    }
                }
                , 0L);




    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmapList != null) {
            canvas.save();
            int x = 0;

            for (int i = 0; i < mBitmapList.size(); i++) {
                Bitmap bitmap = mBitmapList.get(i);

                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, x, 0, null);
                    x = x + bitmap.getWidth();
                }
            }
        }
    }

    public void setVideo(@NonNull Uri data) {
        mVideoUri = data;
    }

    public void setVideo(String videoUrl) {
        mVideoUrl = videoUrl;
    }


    public void setThumbnailList(List<String> thumbnails) {
        this.thumbnails = thumbnails;
    }
}
