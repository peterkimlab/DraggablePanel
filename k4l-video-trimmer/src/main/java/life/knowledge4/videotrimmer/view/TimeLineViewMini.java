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
package life.knowledge4.videotrimmer.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.View;

import java.util.HashMap;

import life.knowledge4.videotrimmer.R;
import life.knowledge4.videotrimmer.utils.BackgroundExecutor;
import life.knowledge4.videotrimmer.utils.UiThreadExecutor;

public class TimeLineViewMini extends View {

    private Uri mVideoUri;
    private String mVideoUrl;
    private int mHeightView;
    private LongSparseArray<Bitmap> mBitmapList = null;
    private Long[] seekArry;
    public static boolean checkPrepare = false;

    public TimeLineViewMini(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLineViewMini(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mHeightView = getContext().getResources().getDimensionPixelOffset(R.dimen.frames_video_height_mini);
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
        final int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();

        int w = resolveSizeAndState(minW, getMeasuredWidth(), 1);


        getBitmap(ww);
    }

    private void getBitmap(final int viewWidth) {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
                                       @Override
                                       public void execute() {
                                           try {
                                               LongSparseArray<Bitmap> thumbnailList = new LongSparseArray<>();

                                               MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                                               mediaMetadataRetriever.setDataSource(getContext(), mVideoUri);

                                               // Retrieve media data
                                               long videoLengthInMs = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;

                                               // Set thumbnail properties (Thumbs are squares)
                                               final int thumbWidth = mHeightView;
                                               final int thumbHeight = mHeightView;
                                               int numThumbs = (int) Math.ceil(((float) viewWidth) / thumbWidth);
                                               final long interval = videoLengthInMs / numThumbs;

                                               seekArry = new Long[numThumbs];
                                               for (int i = 0; i < numThumbs; ++i) {
                                                   Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(i * interval, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                                                   // TODO: bitmap might be null here, hence throwing NullPointerException. You were right
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

    public void setBitmapUrl(int ww){
        final int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();

        int w = resolveSizeAndState(minW, getMeasuredWidth(), 1);


        getBitmapUrlGlide(ww);
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

    private void getBitmapUrlGlide(final int viewWidth) {
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


}
