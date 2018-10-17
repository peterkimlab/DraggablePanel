package com.test.english.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Datums {

        @SerializedName("_index")
        public String index;

        @SerializedName("_type")
        public String type;

        @SerializedName("_id")
        public String id;

        @SerializedName("_score")
        public String score;

        @SerializedName("_source")
        public HashMap<String, Object> source;

        @SerializedName("highlight")
        public HashMap<String, Object> highlight;

        public boolean active;
        public boolean isMusic;
        public boolean isRefresh;

        public List<Datums> relation = new ArrayList<>();

}