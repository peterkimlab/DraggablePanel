package com.test.english.entities;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

/**
 * Created by MADNESS on 5/16/2017.
 */

public class YoutubeChannel {
    @SerializedName("kind")
    public String kind;
    @SerializedName("etag")
    public String etag;
    @SerializedName("pageInfo")
    public PageInfo pageInfo;
    @SerializedName("items")
    public List<HashMap<String, Object>> items;
}
