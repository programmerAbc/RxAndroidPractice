package com.practice.rx;


import android.util.Log;

import com.practice.util.HttpUtil;

import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testRun() throws UnsupportedEncodingException {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://api.douban.com/v2/")
                .client(HttpUtil.getClient())
                .build();
        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        retrofit2.Call<ResponseBody> call=retrofitService.getSearchBooks("小王子","",0,3);
        try {
            String respStr=call.execute().body().string();
            System.out.println("Response Str: " + respStr);
        } catch (IOException e) {
           System.out.println("rxrun27: " + Log.getStackTraceString(e));
        }
    }
}