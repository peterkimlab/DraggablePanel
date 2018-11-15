package com.edxdn.hmsoon.entities;

/**
 * Created by MADNESS on 5/16/2017.
 */

public class ContactModel {
    private String mName;
    private String mImageUrl;
    private String type;
    private int image;
    public boolean active;


    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
