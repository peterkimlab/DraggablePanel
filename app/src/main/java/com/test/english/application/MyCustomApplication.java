package com.test.english.application;

import android.app.Application;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;

import com.test.english.entities.HummingSoonConfig;
import com.test.english.ui.main.MainActivity;

public class MyCustomApplication extends Application {

    public static String TAG = "Humming_";

    private static MyCustomApplication singleton;
    private static MainActivity mainActivity;
    private static HummingSoonConfig hummingSoonConfig;
    //private static PlayerMusicActivity playerMusicActivity;

    public static MyCustomApplication getApplicationInstance(){
        return singleton;
    }

    public static MainActivity getMainInstance(){
        return mainActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    public void setMainInstance(MainActivity m){
        mainActivity = m;
    }

    public static HummingSoonConfig getConfig(){
        return hummingSoonConfig;
    }

    /*public static PlayerMusicActivity getPlayerMusicActivity(){
        return playerMusicActivity;
    } */

    /*
    public void setPlayerMusicActivity(PlayerMusicActivity m){
        playerMusicActivity = m;
    }

    public void setConfig(HummingSoonConfig c){
        hummingSoonConfig = c;
    }*/

}