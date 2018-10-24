package com.test.english.ui.youtube;

import com.test.english.entities.YoutubeChannel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by anupamchugh on 09/01/17.
 */

public interface APIInterfaceYoutube {
    @GET("/youtube/v3/channels")
    Call<YoutubeChannel> getYoutubeChannelInfo(@Query("part") String part, @Query("id") String id, @Query("key") String key);
}
