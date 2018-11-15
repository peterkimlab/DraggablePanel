package com.edxdn.hmsoon.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anupamchugh on 09/01/17.
 */

public class ClipResource {

    @SerializedName("total")
    public Integer total;
    @SerializedName("max_score")
    public double maxScore;
    public List<Datums> hits = new ArrayList<>();

}
