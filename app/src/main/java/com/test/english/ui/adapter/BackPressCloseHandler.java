package com.test.english.ui.adapter;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by hairgely on 2018-03-04.
 */

public class BackPressCloseHandler {

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private Toast toast;
    private Activity activity;

    public BackPressCloseHandler(Activity context){
        this.activity = context;
    }

    public void onBackPressed(){

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime){
            toast.cancel();
            activity.finish();
            ActivityCompat.finishAffinity(activity);
            System.exit(0);
        } else {
            backPressedTime = tempTime;
            showGuide();
        }

    }

    public void showGuide(){
        toast = Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
