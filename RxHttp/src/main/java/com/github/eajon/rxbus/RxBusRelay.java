package com.github.eajon.rxbus;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/17.
 */

public class RxBusRelay {
    //这个是背压设计，可以解决订阅者处理事件出现异常后，订阅者无法再收到事件。
    private final Relay<Object> mBus;

    private RxBusRelay() {
        // toSerialized method made bus thread safe
        mBus = PublishRelay.create().toSerialized();
    }

    public static RxBusRelay get() {
        return Holder.BUS;
    }

    public void post(Object obj) {
        mBus.accept(obj);
    }

    public Flowable<Object> asFlowable() {
        return mBus.toFlowable(BackpressureStrategy.LATEST);
    }
    public Flowable register(Class eventType) {
        return mBus.toFlowable(BackpressureStrategy.LATEST).ofType(eventType);
    }
    public Flowable registerIOMain(Class eventType){
        return mBus.toFlowable(BackpressureStrategy.LATEST).ofType(eventType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }

    private static class Holder {
        private static final RxBusRelay BUS = new RxBusRelay();
    }
}
