package com.test.english.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anupamchugh on 09/01/17.
 */

public class PostResource {

    @SerializedName("_index")
    public String index;

    @SerializedName("_type")
    public String type;

    @SerializedName("_id")
    public String id;

    @SerializedName("_version")
    public String version;

    @SerializedName("result")
    public String result;

}
