package com.practice.rx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.practice.util.DebugUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rxrun10();
    }

    private void rxrun10(){
      Observable.interval(1,TimeUnit.SECONDS).filter(aLong -> aLong%3==0).subscribe(new Subscriber<Long>() {
          @Override
          public void onCompleted() {
              Log.e(TAG, "onCompleted: " );
          }

          @Override
          public void onError(Throwable e) {
              Log.e(TAG, "onError: "+e.getMessage() );
          }

          @Override
          public void onNext(Long aLong) {
              Log.e(TAG, "onNext: "+aLong );
          }
      });

    }

    private void rxrun9() {
        Observable.interval(1, TimeUnit.SECONDS).subscribe(new Subscriber<Long>() {
            long TIME_OUT=20;
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: TIME_OUT!!!\nSTOP SUBSCRIBE");
                unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " );
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "onNext: "+aLong );
                if(aLong>=TIME_OUT){
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
