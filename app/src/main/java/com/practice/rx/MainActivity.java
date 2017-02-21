package com.practice.rx;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.practice.model.FaceResponse;
import com.practice.model.WeatherResponse;
import com.practice.util.DebugUtil;
import com.practice.util.HttpUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.BackpressureOverflow;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 0x10;
    MyTextview myTv;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                rxrunPoint();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
        setContentView(R.layout.activity_main);
        myTv = (MyTextview) findViewById(R.id.myTv);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            rxrunPoint();
        }
    }

    private void rxrunPoint() {
        rxrun35();
    }

    private void rxrun35() {
        Observable.create(subscriber -> {
            try {
                for (int i = 0; i < 1000; ++i) {
                    Thread.sleep(10);
                    Log.e(TAG, "src emit: " + i + ",on Thread:" + Thread.currentThread().getName());
                    subscriber.onNext("item: " + i);
                }
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .onBackpressureBuffer(10, () -> {
                    Log.e(TAG, "detect overflow on Thread:"+Thread.currentThread().getName());
                }, BackpressureOverflow.ON_OVERFLOW_DROP_OLDEST)
                .observeOn(Schedulers.computation())
                .map(o->{
                    Log.e(TAG, "map on Thread: " +Thread.currentThread().getName() );
                    return o;
                })
                .onBackpressureBuffer(10, () -> {
                    Log.e(TAG, "[another buffer]detect overflow on Thread:"+Thread.currentThread().getName());
                }, BackpressureOverflow.ON_OVERFLOW_DROP_OLDEST)
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + Log.getStackTraceString(e));
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(TAG, "subscriber receive:" + o + ",on Thread:" + Thread.currentThread().getName());
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });


    }


    private void rxrun34() throws MalformedURLException {
        File file1 = new File(Environment.getExternalStorageDirectory() + "/Download/1.jpg");
        File file2 = new File(Environment.getExternalStorageDirectory() + "/Download/2.jpg");
        RequestBody requestFile1 = RequestBody.create(MediaType.parse("image/jpg"), file1);
        RequestBody requestFile2 = RequestBody.create(MediaType.parse("image/jpg"), file2);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://v1-auth-api.visioncloudapi.com")
                .client(HttpUtil.getClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        retrofitService.faceAlignment(
                RequestBody.create(MediaType.parse("text/plain"), "db8ab2f4f8b64ccbb48b4314ea70afbb"),
                RequestBody.create(MediaType.parse("text/plain"), "f1b59d98a6144fcea89f98504af9716e"),
                MultipartBody.Part.createFormData("selfie_file", file1.getName(), requestFile1),
                MultipartBody.Part.createFormData("historical_selfie_file", file2.getName(), requestFile2)
        )
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Subscriber<FaceResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(FaceResponse faceResponse) {
                                Log.e(TAG, "onNext: " + faceResponse.getRequestId() + " " + faceResponse.getStatus() + " " + faceResponse.getConfidence());
                            }
                        }
                );
    }

    private void rxrun33() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.baidu.com")
                .client(HttpUtil.getClient())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofitService.downloadFileWithResume("http://sw.bos.baidu.com/sw-search-sp/software/13d93a08a2990/ChromeStandalone_55.0.2883.87_Setup.exe", "bytes=10000-").enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                FileOutputStream fos = null;
                ResponseBody responseBody = response.body();
                long contentLength = responseBody.contentLength();
                Log.e(TAG, "Content-Length= " + (float) contentLength / 1024 / 1024 + "MB");
                float downloadedSize = 0;
                InputStream is = responseBody.byteStream();
                byte[] buffer = new byte[1024];
                int readSize = 0;
                String fileName = "ChromeStandalone_55.0.2883.87_Setup.exe";
                File file = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
                file.delete();
                float downloadedPercentage = 0;
                try {
                    file.createNewFile();
                    fos = new FileOutputStream(file, true);
                    while ((readSize = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, readSize);
                        downloadedSize += readSize;
                        downloadedPercentage = downloadedSize * 100 / contentLength;
                        //       Log.e(TAG, "download percentage: " + downloadedPercentage + "%");
                    }
                    Log.e(TAG, "download completed!!! ");
                } catch (Exception e) {
                    Log.e(TAG, "download error:" + Log.getStackTraceString(e));
                    file.delete();
                } finally {
                    try {
                        fos.flush();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                    }
                    try {
                        fos.close();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                    }
                    try {
                        is.close();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: " + Log.getStackTraceString(t));
            }
        });


    }

    private void rxrun32() {
        PublishSubject<Float> publishSubject = PublishSubject.create();
        publishSubject.distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Float>() {
                    @Override
                    public void onCompleted() {
                        myTv.setText("\u2713");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                    }

                    @Override
                    public void onNext(Float aFloat) {
                        myTv.setProgress(aFloat);
                    }
                });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.baidu.com")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(HttpUtil.getClient())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofitService.downloadFileRx("http://sw.bos.baidu.com/sw-search-sp/software/13d93a08a2990/ChromeStandalone_55.0.2883.87_Setup.exe")
                .observeOn(Schedulers.io())
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        FileOutputStream fos = null;
                        long contentLength = responseBody.contentLength();
                        Log.e(TAG, "Content-Length= " + (float) contentLength / 1024 / 1024 + "MB");
                        float downloadedSize = 0;
                        InputStream is = responseBody.byteStream();
                        byte[] buffer = new byte[1024];
                        int readSize = 0;
                        String fileName = "ChromeStandalone_55.0.2883.87_Setup.exe";
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
                        file.delete();
                        try {
                            file.createNewFile();
                            fos = new FileOutputStream(file, true);
                            while ((readSize = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, readSize);
                                downloadedSize += readSize;
                                //Log.e(TAG, "onResponse: " + downloadedSize * 100 / contentLength);
                                publishSubject.onNext(downloadedSize / contentLength);
                            }
                            publishSubject.onCompleted();
                            //  Log.e(TAG, "download completed!!! ");
                        } catch (Exception e) {
                            Log.e(TAG, "download error:" + Log.getStackTraceString(e));
                            publishSubject.onError(e);
                            file.delete();
                        } finally {
                            try {
                                fos.flush();
                            } catch (Exception e) {
                                Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                            }
                            try {
                                fos.close();
                            } catch (Exception e) {
                                Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                            }
                            try {
                                is.close();
                            } catch (Exception e) {
                                Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                            }
                        }
                    }
                });


    }

    //这种下载实现好像有问题rxrun32的RxJava模式效果不错
    private void rxrun31() {
        PublishSubject<Long> publishSubject = PublishSubject.create();
        publishSubject.distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "downloaded");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, "downloading: " + aLong + "%");
                    }
                });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.baidu.com")
                .client(HttpUtil.getClient())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofitService.downloadFile("http://sw.bos.baidu.com/sw-search-sp/software/13d93a08a2990/ChromeStandalone_55.0.2883.87_Setup.exe").enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                myTv.setText("START DOWNLOAD!!!");
                FileOutputStream fos = null;
                ResponseBody responseBody = response.body();
                long contentLength = responseBody.contentLength();
                Log.e(TAG, "Content-Length= " + (float) contentLength / 1024 / 1024 + "MB");
                long downloadedSize = 0;
                InputStream is = responseBody.byteStream();
                byte[] buffer = new byte[1024];
                int readSize = 0;
                String fileName = "ChromeStandalone_55.0.2883.87_Setup.exe";
                File file = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
                file.delete();
                try {
                    file.createNewFile();
                    fos = new FileOutputStream(file, true);
                    while ((readSize = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, readSize);
                        downloadedSize += readSize;
                        Log.e(TAG, "onResponse: " + downloadedSize * 100 / contentLength);
                        publishSubject.onNext(downloadedSize * 100 / contentLength);
                    }
                    publishSubject.onCompleted();
                    //  Log.e(TAG, "download completed!!! ");
                } catch (Exception e) {
                    Log.e(TAG, "download error:" + Log.getStackTraceString(e));
                    publishSubject.onError(e);
                    file.delete();
                } finally {
                    try {
                        fos.flush();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                    }
                    try {
                        fos.close();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                    }
                    try {
                        is.close();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: " + Log.getStackTraceString(t));
                publishSubject.onError(t);
            }
        });


    }

    private void rxrun30() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.weather.com.cn/")
                .client(HttpUtil.getClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofitService.getCurrentWeatherInfoRx("101010100")
                .subscribeOn(Schedulers.io())
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Subscriber<WeatherResponse>() {
                            @Override
                            public void onCompleted() {
                                Log.e(TAG, "onCompleted: ");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e);
                            }

                            @Override
                            public void onNext(WeatherResponse weatherResponse) {
                                Log.e(TAG, "rxrun30: " + weatherResponse.getWeatherinfo().toString());
                            }
                        }
                );
    }

    private void rxrun29() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.weather.com.cn/")
                .client(HttpUtil.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofitService.getCurrentWeatherInfo("101010100").enqueue(new retrofit2.Callback<WeatherResponse>() {
            @Override
            public void onResponse(retrofit2.Call<WeatherResponse> call, retrofit2.Response<WeatherResponse> response) {
                Log.e(TAG, "onResponse: \n" + response.body().getWeatherinfo().toString());
            }

            @Override
            public void onFailure(retrofit2.Call<WeatherResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t);
            }
        });


    }

    private void rxrun28() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.baidu.com")
                .client(HttpUtil.getClient())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofitService.downloadFile("http://sw.bos.baidu.com/sw-search-sp/software/13d93a08a2990/ChromeStandalone_55.0.2883.87_Setup.exe").enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                FileOutputStream fos = null;
                ResponseBody responseBody = response.body();
                long contentLength = responseBody.contentLength();
                Log.e(TAG, "Content-Length= " + (float) contentLength / 1024 / 1024 + "MB");
                float downloadedSize = 0;
                InputStream is = responseBody.byteStream();
                byte[] buffer = new byte[1024];
                int readSize = 0;
                String fileName = "ChromeStandalone_55.0.2883.87_Setup.exe";
                File file = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
                file.delete();
                float downloadedPercentage = 0;
                try {
                    file.createNewFile();
                    fos = new FileOutputStream(file, true);
                    while ((readSize = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, readSize);
                        downloadedSize += readSize;
                        downloadedPercentage = downloadedSize * 100 / contentLength;
                        Log.e(TAG, "download percentage: " + downloadedPercentage + "%");
                    }
                    Log.e(TAG, "download completed!!! ");
                } catch (Exception e) {
                    Log.e(TAG, "download error:" + Log.getStackTraceString(e));
                    file.delete();
                } finally {
                    try {
                        fos.flush();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                    }
                    try {
                        fos.close();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                    }
                    try {
                        is.close();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: " + Log.getStackTraceString(t));
            }
        });


    }

    private void rxrun27() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.douban.com/v2/")
                .client(HttpUtil.getClient())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofit2.Call<ResponseBody> call = retrofitService.getSearchBooks("Android", "", 0, 3);
        try {
            String respStr = call.execute().body().string();
            Log.e(TAG, "Response Str: " + respStr);
        } catch (IOException e) {
            Log.e(TAG, "rxrun27: " + Log.getStackTraceString(e));
        }

    }

    private void rxrun26() {
        HttpUtil.asynGetRequest("http://sw.bos.baidu.com/sw-search-sp/software/13d93a08a2990/ChromeStandalone_55.0.2883.87_Setup.exe", new HashMap(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: " + Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                long contentLength = responseBody.contentLength();
                Log.e(TAG, "Content-Length= " + (float) contentLength / 1024 / 1024 + "MB");
                float downloadedSize = 0;
                InputStream in = responseBody.byteStream();
                byte[] buffer = new byte[1024];
                int readSize = 0;
                String fileName = "ChromeStandalone_55.0.2883.87_Setup.exe";
                File file = new File(Environment.getExternalStorageDirectory() + "/" + fileName);
                file.delete();
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file, true);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        call.cancel();
                    }
                }.start();
                float downloadedPercentage = 0;
                try {
                    while ((readSize = in.read(buffer)) != -1) {
                        fos.write(buffer, 0, readSize);
                        downloadedSize += readSize;
                        downloadedPercentage = downloadedSize * 100 / contentLength;
                        Log.e(TAG, "download percentage: " + downloadedPercentage + "%");
                    }
                    Log.e(TAG, "download completed!!! ");
                } catch (Exception e) {
                    Log.e(TAG, "download error:" + Log.getStackTraceString(e));
                    file.delete();
                } finally {
                    try {
                        fos.flush();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                    }
                    try {
                        fos.close();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                    }
                    try {
                        in.close();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                    }
                    try {
                        responseBody.close();
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + Log.getStackTraceString(e));
                    }
                }
            }
        });


    }

    private void rxrun25() {
        Observable.create(subscriber -> {
            try {
                for (int i = 0; i < 1000; ++i) {
                    Thread.sleep(100);
                    subscriber.onNext("item: " + i);
                    Log.e(TAG, "rxrun25: " + i);
                }
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        })
                .subscribeOn(Schedulers.newThread())
                .onBackpressureBuffer(10)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        myTv.setText(Log.getStackTraceString(e));
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.e(TAG, "onNext: " + o);
                        myTv.setText(o.toString());

                    }
                });
    }

    private void rxrun24() {
        Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
        observable.startWith(-1l, -1l, -1l, -1l).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e);
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext: " + aLong);
            }
        });


    }

    private void rxrun23() {
        Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
        Observable.combineLatest(observable.skip(2).sample(2, TimeUnit.SECONDS), observable.sample(3, TimeUnit.SECONDS), (aLong, aLong2) -> "<" + aLong + "," + aLong2 + ">").subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e);
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: " + s);
            }
        });

    }

    private void rxrun22() {
        Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
        observable.join(observable.sample(2, TimeUnit.SECONDS), aLong -> Observable.timer(2, TimeUnit.SECONDS), aLong -> Observable.timer(2, TimeUnit.SECONDS), (aLong, aLong2) -> aLong.toString() + aLong2.toString()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: " + s.toString());
            }
        });

    }


    private void rxrun21() {
        Observable<Long> observable = Observable.interval(10, TimeUnit.MILLISECONDS).onBackpressureBuffer();
        Observable.zip(observable.map(aLong -> aLong * aLong), observable.sample(1, TimeUnit.SECONDS), (aLong, aLong2) -> "{" + aLong + "," + aLong2 + "}").subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e);
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: " + s);
            }
        });

    }

    private void rxrun20() {
        Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
        Observable<Long> mergedObservable = Observable.merge(observable, observable.sample(2, TimeUnit.SECONDS).map(aLong -> aLong * 10));
        mergedObservable.subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext: " + aLong);
            }
        });
    }

    private void rxrun19() {
        Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
        observable.cast(String.class).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e);
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: " + s);
            }
        });
    }

    private void rxrun18() {
        Observable<List<Long>> observable = Observable.interval(1, TimeUnit.SECONDS).buffer(3, 4);
        observable.subscribe(new Subscriber<List<Long>>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e);
            }

            @Override
            public void onNext(List<Long> longs) {
                Log.e(TAG, "onNext: " + longs.toString());
            }
        });
    }

    private void rxrun17() {
        Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
        observable.scan((aLong, aLong2) -> aLong + aLong2).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e);
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext: " + aLong);
            }
        });
    }


    private void rxrun16() {
        Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
        observable.map(aLong -> aLong * aLong).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e);
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext: " + aLong);
            }
        });
    }


    private void rxrun15() {
        Observable<Long> observable = Observable.interval(3, TimeUnit.SECONDS);
        observable.timeout(2, TimeUnit.SECONDS).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted1: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError1:" + e.getMessage());
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext1: " + aLong);
            }
        });
        observable.timeout(4, TimeUnit.SECONDS).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted2: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError2: " + e.getMessage());
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext2: " + aLong);
            }
        });
    }

    private void rxrun14() {
        Observable<Long> observable = Observable.interval(100, TimeUnit.MILLISECONDS);
        observable.sample(1, TimeUnit.SECONDS).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted1: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError1: ");
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext1: " + aLong);
            }
        });
        observable.sample(2, TimeUnit.SECONDS).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted2: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError2: " + e.getMessage());
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext2: " + aLong);
            }
        });
    }


    private void rxrun13() {
        Observable.interval(1, TimeUnit.SECONDS).elementAt(3).subscribe(new Subscriber<Long>() {

            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: TIME_OUT!!!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext: " + aLong);
            }
        });


    }

    private void rxrun12() {
        Observable.interval(1, TimeUnit.SECONDS).skip(3).subscribe(new Subscriber<Long>() {

            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: TIME_OUT!!!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext: " + aLong);
            }
        });
    }


    private void rxrun11() {
        Observable.interval(1, TimeUnit.SECONDS).take(20).subscribe(new Subscriber<Long>() {

            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: TIME_OUT!!!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext: " + aLong);
            }
        });
    }


    private void rxrun10() {
        Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
        observable.filter(aLong -> aLong % 3 == 0).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext3: " + aLong);
            }
        });
        observable.filter(aLong -> aLong % 2 == 0).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext2: " + aLong);
            }
        });
    }

    private void rxrun9() {
        Observable.interval(1, TimeUnit.SECONDS).subscribe(new Subscriber<Long>() {
            long TIME_OUT = 20;

            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: TIME_OUT!!!\nSTOP SUBSCRIBE");
                unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext: " + aLong);
                if (aLong >= TIME_OUT) {
                    onCompleted();
                }
            }
        });
    }

    private void rxrun8() {
        Observable<Integer> observable = Observable.range(10, 100);
        observable.subscribe(
                new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext: " + integer);
                    }
                }
        );
        observable.subscribe(
                new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext: " + integer);
                    }
                }
        );
    }

    private void rxrun7() {
        PublishSubject<String> publishSubject = PublishSubject.create();
        publishSubject.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: " + s);
            }
        });
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Log.e(TAG, "call: 0");
                subscriber.onNext(0);
                Log.e(TAG, "call: 1");
                subscriber.onNext(1);
                Log.e(TAG, "call: 2");
                subscriber.onNext(2);
                Log.e(TAG, "call: 3");
                subscriber.onNext(3);
                subscriber.onCompleted();
            }
        }).doOnCompleted(new Action0() {
            @Override
            public void call() {
                Log.e(TAG, "doOnCompleted");
                publishSubject.onNext("Hello!!!");
            }
        });
        observable.subscribe();
        observable.subscribe();


    }

    private void rxrun6() {
        PublishSubject<String> publishSubject = PublishSubject.create();
        publishSubject.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: " + s);
            }
        });
        publishSubject.onNext("Item 1");
        publishSubject.onNext("Item 2");
        publishSubject.onNext("Item 3");
        publishSubject.onNext("Item 4");
        publishSubject.onError(new Exception("发生异常"));
    }

    private void rxrun5() {
        Observable observable = Observable.never();
        observable.subscribe(new Observer() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onNext(Object o) {
                Log.e(TAG, "onNext: " + o);
            }
        });

    }


    private void rxrun4() {
        Observable observable = Observable.empty();
        observable.subscribe(new Observer() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onNext(Object o) {
                Log.e(TAG, "onNext: " + o);
            }
        });

    }

    private void rxrun3() {
        Observable<String> observable = Observable.just("String1", "String2", "String3", "String4", "String5");
        observable.repeat(3).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: " + s);
            }
        });
    }


    private void rxrun2() {
        Observable<String> observable = Observable.from(DebugUtil.generateDummyData(100));
        observable.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: " + s);
            }
        });
    }


    private void rxrun() {
        Observable<Integer> observableString = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 50; ++i) {
                    subscriber.onNext(i);
                }
                subscriber.onCompleted();
            }
        });
        observableString.subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "onNext: " + integer);
            }
        });
    }
}
