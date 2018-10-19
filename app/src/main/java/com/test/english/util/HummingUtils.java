package com.test.english.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;
import com.test.english.api.Datums;
import com.test.english.application.MyCustomApplication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by hairg on 2018-05-04.
 */

public class HummingUtils {

    public static String APP_SAVE_NAME = "SaveAppData";
    public static String APP_SAVE_SEARCH_RECORD = "RecordSearchData";
    public static String IMAGE_PATH = "https://dwzgxwn7s5fir.cloudfront.net/";

    public static boolean isEmpty(TextView time) {
        if(time == null){
            return true;
        }
        return time.getText().toString().equals("") ? true : false;
    }

    public static class ElasticField {
        public static final String IDS = "ids";
        public static final String TITLE = "title";
        public static final String VIDEO_URL = "videourl";
        public static final String THUMBNAIL_URL = "thumbnalurl";
        public static final String THUMBNAILS = "thumbnails";
        public static final String TEXT_EN = "texten";
        public static String TEXT_LO = "textko";

        public static final String PATTERN = "pattern";
        public static final String WORD = "word";
        public static final String GENRE = "genre";

        public static final String STYPE = "stype";
        public static final String ICODE = "icode";
        public static final String RCODE = "rcode";
        public static final String SCODE = "scode";
        public static final String CONTENTSDATE = "contentsdate";
        public static final String CONVERSATION = "conversation";

        public static final String SPEAK_KO = "speakko";
        public static final String SPEAK_KO2 = "speakko2";
        public static final String SPEAKER = "speaker";
        public static final String NCODE = "ncode";
        public static final String AUDIO_URL = "audiourl";
        public static final String YOUTUBE_ID = "youtubeid";
        public static final String YOUTUBE_CHANNEL_ID = "channel";
        public static final String TODAYDATE = "todaydate";
        public static final String AUTHOR = "author";
        public static final String VTYPE = "vtype";

        public static final String STIME = "stime";
        public static final String ETIME = "etime";

    }

    public static class VideoType {
        public static final String YOUTUBE = "youtube";
        public static final String YOUTUBE_MUSIC = "youtube_music";
        public static final String MOVIE = "moview";
        public static final String DRAMA = "drama";
        public static final String MUSIC = "music";
    }

    public static class SharedData {
        public static final String GUEST_LOGN = "GUEST_LOGN";

    }

    public static String getExtension(String fileStr){
        String fileExtension = fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.length());
        return TextUtils.isEmpty(fileExtension) ? null : fileExtension;
    }

    /*public static String translateText (String text, String baseLang, String targetLang){
        TranslateOptions options = TranslateOptions.newBuilder().setApiKey("AIzaSyAhAECrmHzCWbZlg2P60l_2vOsxakLP5HQ") .build();
        Translate translate = options.getService();
        //Translate translate = TranslateOptions.getDefaultInstance().getService();
        // Translates some text into Russian
        Translation translation =
                translate.translate(
                        text,
                        Translate.TranslateOption.sourceLanguage(baseLang),
                        Translate.TranslateOption.targetLanguage(targetLang));


        return translation.getTranslatedText();
    }*/

    public static void requestPermission(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
        }
    }

    public static String time(String stime, String etime) {
        BigDecimal s = new BigDecimal(stime);
        BigDecimal e = new BigDecimal(etime);
        BigDecimal t = e.subtract(s);
        if(t.intValue() == 0){
            t = new BigDecimal(1);
        }

        return milliSecondsToTimer(t.intValue() * 1000);
    }


    public static String milliSecondsToTimer(long milliseconds) {
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

    public static String removeSpecialChar(String str){
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        str =str.replaceAll(match, " ");
        return str;
    }
    public static String removeSpecialCharVoice(String str) {
        str = str.replaceAll("\\.", "");
        str = str.replaceAll(",", "");
        return str;
    }


    public static void saveShared_Data(Context context, String key, String data) {
        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(key, data);
        edit.commit();
    }

    public static void saveShared_BooleanData(Context context, String key, boolean data) {
        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(key, data);
        edit.commit();
    }

    public static boolean loadShared_BooleanData(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(key, false);

    }

    //꺼내기
    public static String loadShared_Data(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Context.MODE_PRIVATE);
        return pref.getString(key, null);

    }

    //저장
    public static void saveSharedPreferences_Data(Context context, String key, ArrayList<String> dic) {
        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(dic);
        edit.putStringSet(key, set);
        edit.commit();


    }

    //꺼내기
    public static ArrayList<String> loadSharedPreferencesData(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Context.MODE_PRIVATE);
        Set<String> set = pref.getStringSet(key, null);
        if(set == null){
            return new ArrayList<String>();
        }else{
            return new ArrayList<String>(set);
        }

    }

    public static void deleteSharedPreferencesData(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(APP_SAVE_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }


    public static String getLocale(Context context) {
        Locale mLocale = context.getResources().getConfiguration().locale;
        return mLocale.getLanguage();
    }

    public static boolean isLocaleKo(Context context) {
        String mLocale = getLocale(context);
        return mLocale.contains("ko") ? true : false;
    }


    public static String getTitle(Datums playlistObject, Context context) {
        String text = "";
        if(playlistObject.source.containsKey(ElasticField.TITLE)){
            text = playlistObject.source.get(ElasticField.TITLE).toString().replaceAll("_", " ");
        }

        return text == null ? "" : text;
    }

    public static String getSentence(Datums playlistObject, Context context) {
        String text = "";
        if(playlistObject.source.containsKey(ElasticField.TEXT_EN)){
            text = playlistObject.source.get(ElasticField.TEXT_EN).toString();
        }

        return text == null ? "" : text;
    }

    public static String getSentenceLo(Datums playlistObject, Context context) {
        String text = "";
        if(playlistObject.source.containsKey(ElasticField.TEXT_LO)){
            text = playlistObject.source.get(ElasticField.TEXT_LO).toString();
        }

        return text == null ? "" : text;
    }

    public static String getSpeakLo(Datums playlistObject, Context context) {
        String text = "";
        if(playlistObject.source.containsKey(ElasticField.SPEAK_KO)){
            text = playlistObject.source.get(ElasticField.SPEAK_KO).toString();
        }
        if(text == null || text.equals("")){
            if(playlistObject.source.containsKey(ElasticField.SPEAK_KO2)){
                text = playlistObject.source.get(ElasticField.SPEAK_KO2).toString();
            }
        }

        return text == null ? "" : text;
    }

    public static String getTime(Datums playlistObject, Context context) {
        String text = "";
        if (playlistObject.source.get(HummingUtils.ElasticField.STIME) != null && playlistObject.source.get(HummingUtils.ElasticField.ETIME) != null) {
            text = HummingUtils.time(playlistObject.source.get(HummingUtils.ElasticField.STIME).toString(), playlistObject.source.get(HummingUtils.ElasticField.ETIME).toString());
        }

        return text == null ? "" : text;
    }

    public static String getSentenceByMode(Datums playlistObject, Context context) {

        String text = "";
        int mode = MyCustomApplication.getMainInstance().getSpeakLo();

        if (HummingUtils.getSpeakLo(playlistObject, context).equals("")) {
            mode = 6;
        }

        switch (mode) {
            case 0:
                text = HummingUtils.getSpeakLo(playlistObject, context);
                break;
            case 1:
                text = HummingUtils.getSpeakLo(playlistObject, context);
                break;
            case 2:
                text = HummingUtils.getSentence(playlistObject, context);
                break;
            case 3:
                text = HummingUtils.getSentence(playlistObject, context);
                break;
            case 4:
                text = HummingUtils.getSentenceLo(playlistObject, context);
                break;
            case 5:
                text = HummingUtils.getSentenceLo(playlistObject, context);
                break;
            case 6:
                text = HummingUtils.getSentence(playlistObject, context);
                break;
        }
        return text == null ? "" : text;
    }

    public static String getTitleByMode(Datums playlistObject, Context context) {

        String text = "";
        int mode = MyCustomApplication.getMainInstance().getSpeakLo();

        if(HummingUtils.getSpeakLo(playlistObject, context).equals("")){
            mode = 6;
        }

        switch (mode) {
            case 0:
                text = HummingUtils.getSentenceLo(playlistObject, context);
                break;
            case 1:
                text = HummingUtils.getSentence(playlistObject, context);
                break;
            case 2:
                text = HummingUtils.getSentenceLo(playlistObject, context);
                break;
            case 3:
                text = HummingUtils.getSpeakLo(playlistObject, context);
                break;
            case 4:
                text = HummingUtils.getSentence(playlistObject, context);
                break;
            case 5:
                text = HummingUtils.getSpeakLo(playlistObject, context);
                break;
            case 6:
                text = HummingUtils.getTitle(playlistObject, context);
                break;
        }

        return text == null ? "" : text;

    }

}
