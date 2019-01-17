package com.github.eajon.observer;

import com.github.eajon.exception.ApiException;
import com.github.eajon.util.LogUtils;

import io.reactivex.observers.DisposableObserver;

public abstract class HttpObserver<T> extends DisposableObserver <T> {


    public abstract void onSuccess(T response);

    public abstract void onError(ApiException exception);



    @Override
    public void onNext(T value) {
        LogUtils.e("success:" + value.toString());
        onSuccess(value);

    }

    @Override
    public void onError(Throwable e) {
        LogUtils.e("error:" + e.getMessage());
        onError(ApiException.handleException(e));
        dispose();
    }

    @Override
    public void onComplete() {
        LogUtils.d("onComplete");
        dispose();
    }
}
