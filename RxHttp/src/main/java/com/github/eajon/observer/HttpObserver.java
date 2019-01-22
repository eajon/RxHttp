package com.github.eajon.observer;

import com.github.eajon.exception.ApiException;

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
        onError(( ApiException ) e);
        dispose();
    }

    @Override
    public void onComplete() {
        dispose();
    }
}
