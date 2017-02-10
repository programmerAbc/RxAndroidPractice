package com.practice.rx;


import com.practice.model.FaceResponse;
import com.practice.model.WeatherResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithResume(@Url String url, @Header("RANGE") String range);

    @Multipart
    @POST("identity/historical_selfie_verification")
    Observable<FaceResponse> faceAlignment(
            @Part("api_id") RequestBody apiId,
            @Part("api_secret") RequestBody apiSeret,
            @Part MultipartBody.Part file1,
            @Part MultipartBody.Part file2
    );
}
