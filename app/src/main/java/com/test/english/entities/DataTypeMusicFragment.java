package com.test.english.entities;

/**
 * Created by Mukesh on 3/8/17.
 * himky02@gmail.com
 */

public class DataTypeMusicFragment {
    public static final int IMAGE_TYPE = 1;
    public static final int TEXT_TYPE = 2;
    public int type;
    public int data;
    public String text;

    public DataTypeMusicFragment(int type, String text, int data) {
        this.type = type;
        this.data = data;
        this.text = text;
    }
}
