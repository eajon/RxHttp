package com.github.eajon.observer;


import com.github.eajon.RxHttp;
import com.github.eajon.util.LogUtils;

public abstract class HttpObserver<T> extends BaseObserver<T> {
    private static final String TAG = "HttpObserver";


    @Override
    public void onNext(T value) {
            onSuccess(value);

    }

    @Override
    public void onError(Throwable e) {
        LogUtils.e(RxHttp.getConfig().getLogTag(), "error:" + e.toString());
        onError(e.toString());
        dispose();
    }

    @Override
    public void onComplete() {
        LogUtils.d(RxHttp.getConfig().getLogTag(), "onComplete");
        dispose();

    }

    protected abstract void onSuccess(T t);

    protected abstract void onError(String t);





}



