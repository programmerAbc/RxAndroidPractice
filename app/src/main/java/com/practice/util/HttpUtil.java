package com.practice.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gaofeng on 2016-07-26.
 */
public class HttpUtil {
    private static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(4, TimeUnit.SECONDS).readTimeout(4, TimeUnit.SECONDS).writeTimeout(4, TimeUnit.SECONDS).build();

    public static OkHttpClient getClient() {
        return client;
    }


    public static void asynPostRequest(String url, Map<String, String> requestParams, Callback callback) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            formBodyBuilder.add(entry.getKey(), entry.getValue());
        }
        Request request = new Request.Builder().url(url).post(formBodyBuilder.build()).build();
        client.newCall(request).enqueue(callback);
    }

    public static String syncPostRequest(String url, Map<String, String> requestParams) {
        String responseBodyStr = null;
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            formBodyBuilder.add(entry.getKey(), entry.getValue());
        }
        Request request = new Request.Builder().url(url).post(formBodyBuilder.build()).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                responseBodyStr = response.body().string();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.body().close();
            }
        }
        return responseBodyStr;
    }

    public static void asynGetRequest(String url, Map<String, String> requestParams, Callback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        client.newCall(request).enqueue(callback);
    }

    public static String syncGetRequest(String url, Map<String, String> requestParams) {
        String responseBodyStr = null;
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                responseBodyStr = response.body().string();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.body().close();
            }
        }
        return responseBodyStr;
    }


    private HttpUtil() {
    }
}
