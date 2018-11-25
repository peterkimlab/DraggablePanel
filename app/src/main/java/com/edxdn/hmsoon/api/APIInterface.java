package com.edxdn.hmsoon.api;

import com.edxdn.hmsoon.entities.HummingSoonConfig;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by anupamchugh on 09/01/17.
 */

public interface APIInterface {

    @GET("/search/config")
    Call<HummingSoonConfig> doGetConfig();

    @GET("/search/sentence")
    Single<SearchResource> getSentences(@Query("page") String page, @Query("sentence") String sentence, @Query("sort") String sort, @Query("genre") String genre);

    @GET("/search/pattern")
    Single<SearchResource> getPatterns(@Query("page") String page, @Query("pattern") String pattern);

    @GET("/search/word")
    Call<SearchResource> getWords(@Query("page") String page, @Query("word") String word);

    @GET("/search/genre")
    Call<SearchResource> getGenres(@Query("page") String page, @Query("genres") String genres);

    @GET("/search/interests")
    Single<SearchResource> getInterests(@Query("page") String page, @Query("interest") String interest);

    @GET("/search/interest")
    Call<SearchResource> getInterest(@Query("page") String page, @Query("icode") String icode);

    @GET("/search/favorite")
    Call<SearchResource> getFavorite(@Query("page") String page, @Query("email") String email);

    @GET("/search/watched")
    Call<SearchResource> getWatched(@Query("page") String page, @Query("email") String email);

    @POST("/search/favorite")
    Call<PostResource> postFavorite(@Query("email") String email, @Query("ids") String ids); // 패이보릿 목록 서버에 저장

    @PUT("/search/favorite")
    Call<PostResource> putFavorite(@Query("email") String email, @Query("ids") String ids);

    @GET("/search/today")
    Call<SearchResource> getToday(@Query("page") String page, @Query("tcode") String tcode);

    @GET("/search/rankings")
    Call<SearchResource> getRankings(@Query("page") String page, @Query("ranking") String interest);

    @GET("/search/ranking")
    Call<SearchResource> getRanking(@Query("page") String page, @Query("rcode") String rcode);

    @GET("/search/popular")
    Single<SearchResource> getPopular(@Query("page") String page, @Query("sentence") String sentence, @Query("sort") String sort);

    @GET("/search/contents")
    Single<SearchResource> getContents(@Query("page") String page, @Query("vtype") String vtype);

    @PUT("/search/sentenceko")
    Call<PostResource> putSentenceKo(@Query("ids") String ids, @Query("textko") String textko, @Query("textjp") String textjp, @Query("speakko") String speakko);

    @GET("/search/speak")
    Call<SearchResource> getSpeak(@Query("sentence") String sentence);

    @GET("/search/episode")
    Call<SearchResource> doGetEpisodeList(@Query("page") String page, @Query("ids") String ids);

    @GET("/search/search")
    Call<SearchResource> doGetSearch(@Query("size") String size, @Query("sentence") String sentence);

    @GET("/search/searchhistory")
    Call<SearchResource> doGetSearchHelper(@Query("size") String size, @Query("sentence") String sentence);

    @GET("/search/searchhistory")
    Call<SearchResource> doGetSearchHistory(@Query("size") String size, @Query("sentence") String sentence);

    @POST("/search/searchhistory")
    Call<PostResource> postSearchHistory(@Query("email") String email, @Query("texten") String texten);

    @POST("/search/click")
    Call<PostResource> postClick(@Query("email") String email, @Query("ids") String ids);

    @POST("/search/play")
    Call<PostResource> postPlay(@Query("ids") String ids);

    @GET("/search/sentence")
    Call<SearchResource> doGetSentenceList(@Query("page") String page, @Query("sentence") String sentence, @Query("ids") String ids);

    @GET("/search/random")
    Call<SearchResource> getRandom(@Query("page") String page, @Query("sentence") String sentence);

    @GET("/search/discover")
    Call<List<SearchResource>> getDiscover(@Query("page") String page, @Query("pattern") String pattern);

    @GET("/search/firebaseauth")
    Call<HummingSoonConfig> getToken(@Query("token") String token);

    @GET("/search/relation?size=30")
    Call<SearchResource> getRelation(@Query("page") String page, @Query("sentence") String sentence, @Query("ids") String ids);

    @GET("/search/sentence")
    Call<SearchResource> doSearchDataList(@Query("page") String page, @Query("sentence") String sentence);

    @GET("/search/sentence")
    Call<SearchResource> doGetFirstDataList(@Query("page") String page, @Query("sentence") String sentence);

    @GET("/search/sentence")
    Call<SearchResource> doGetDiscoveryList(@Query("page") String page);

    @GET("/search/sentence")
    Call<SearchResource> doGetAnalyticsList(@Query("page") String page);

    @GET("/search/sentence")
    Call<SearchResource> doGetLearnList(@Query("videoNo") String videoNo);

    @GET("/search/sentence")
    Call<SearchResource> doGetLearnPlayList(@Query("sentence") String sentence);

    @GET("/search/sentence")
    Call<SearchResource> doGetTrainingList(@Query("page") String page, @Query("videoNo") String videoNo);

}
