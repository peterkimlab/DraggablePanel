package com.test.english.ui.data;

public class ExploreFragmentItemModel {

    private int vType;

    private String vtitle;
    private String item_thumbnail;
    private String time;
    private String sentence;
    public ExploreFragmentItemModel(int vType, String vtitle, String item_thumbnail, String time, String sentence) {
        this.vType = vType;
        this.vtitle = vtitle;
        this.item_thumbnail = item_thumbnail;
        this.time = time;
        this.sentence = sentence;
    }

    public int getvType() {
        return vType;
    }

    public void setvType(int vType) {
        this.vType = vType;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getVtitle() {
        return vtitle;
    }

    public void setVtitle(String vtitle) {
        this.vtitle = vtitle;
    }

    public String getItem_thumbnail() {
        return item_thumbnail;
    }

    public void setItem_thumbnail(String item_thumbnail) {
        this.item_thumbnail = item_thumbnail;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
