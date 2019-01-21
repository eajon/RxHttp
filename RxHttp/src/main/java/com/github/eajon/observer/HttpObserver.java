package com.github.eajon.observer;

import com.github.eajon.exception.ApiException;
import com.github.eajon.util.LoggerUtils;

import io.reactivex.observers.DisposableObserver;

public abstract class HttpObserver<T> extends DisposableObserver <T> {


    public abstract void onSuccess(T response);

    public abstract void onError(ApiException exception);



    @Override
    public void onNext(T value) {
        onSuccess(value);

    }

    @Override
    public void onError(Throwable e) {
        LoggerUtils.error("error:" + e.getMessage());
        onError(ApiException.handleException(e));
        dispose();
    }

    @Override
    public void onComplete() {
        LoggerUtils.info("onComplete");
        dispose();
    }
}
