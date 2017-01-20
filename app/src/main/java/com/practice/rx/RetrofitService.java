package com.practice.rx;


import com.practice.model.WeatherResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by gaofeng on 2017-01-17.
 */

public interface RetrofitService {
    @GET("book/search")
    Call<ResponseBody> getSearchBooks(@Query("q") String name, @Query("tag") String tag, @Query("start") int start, @Query("count") int count);

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String url);

    @GET("data/sk/{cityNO}.html")
    Call<WeatherResponse> getCurrentWeatherInfo(@Path("cityNO") String cityNO);

    @GET("data/sk/{cityNO}.html")
    Observable<WeatherResponse> getCurrentWeatherInfoRx(@Path("cityNO") String cityNO);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFileRx(@Url String url);

}
