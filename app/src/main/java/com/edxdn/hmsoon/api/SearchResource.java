package com.edxdn.hmsoon.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anupamchugh on 09/01/17.
 */

public class SearchResource {

    @SerializedName("took")
    public Integer took;
    @SerializedName("hits")
    public ClipResource hits;
    @SerializedName("typeName")
    public String typeName;


}
