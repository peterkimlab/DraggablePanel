package com.edxdn.hmsoon.ui.data;

/**
 * Created by Mukesh on 3/8/17.
 * himky02@gmail.com
 */

public class DataTypeMusicFragment {

    public static final int IMAGE_TYPE = 1;
    public static final int TEXT_TYPE = 2;

    public static final String EXPLORE_SENTENCE_TYPE = "sentence";
    public static final String EXPLORE_PATTERN_TYPE = "pattern";
    public static final String POPULAR_TYPE = "popular";
    public static final String CHAT_TYPE = "chat";

    public static final String MOTHERGOOSE_TYPE = "mothergoose";
    public static final String SENTENCE_TYPE = "sentence";


    public static final String WORDS_TYPE = "words";
    public static final String GENRES_TYPE = "genres";

    public int type;
    public int data;
    public String text;

    public DataTypeMusicFragment(int type, String text, int data) {
        this.type = type;
        this.data = data;
        this.text = text;
    }
}
