package com.github.eajon.observer;


import com.github.eajon.RxHttp;
import com.github.eajon.util.LogUtils;

public abstract class HttpObserver<T> extends BaseObserver<T> {

    private static final String TAG = "HttpObserver";
    public abstract void onCancel();
}



