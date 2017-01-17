package com.practice.rx;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by gaofeng on 2017-01-17.
 */

public interface RetrofitService {
    @GET("book/search")
    Call<ResponseBody> getSearchBooks(@Query("q") String name, @Query("tag") String tag, @Query("start") int start, @Query("count") int count);
    @GET
    Call<ResponseBody> downloadFile(@Url String url);
}
