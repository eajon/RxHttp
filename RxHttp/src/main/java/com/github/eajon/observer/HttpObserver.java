package com.github.eajon.observer;


import com.github.eajon.RxHttp;
import com.github.eajon.exception.ApiException;
import com.github.eajon.exception.ExceptionEngine;
import com.github.eajon.util.LogUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class HttpObserver<T> implements Observer <T> {


    public abstract void onSuccess(T t);

    public abstract void onError(ApiException t);

    public abstract void onCancelOrPause();

    private Disposable disposable;

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
    }


    public void dispose() {
        LogUtils.e("dialog", " dispose" );
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            LogUtils.e("dialog", " success" );
        }
    }

    @Override
    public void onNext(T value) {
        LogUtils.e(RxHttp.getConfig().getLogTag(), "success:" + value.toString());
        LogUtils.e("dialog", " observer onNext" );
        onSuccess(value);

    }

    @Override
    public void onError(Throwable e) {
        LogUtils.e(RxHttp.getConfig().getLogTag(), "error:" + e.getMessage());
        LogUtils.e("dialog", "observer onError");
        onError(ExceptionEngine.handleException(e));
        dispose();
    }

    @Override
    public void onComplete() {
        LogUtils.d(RxHttp.getConfig().getLogTag(), "onComplete");
        LogUtils.e("dialog", "observer onComplete");
        dispose();


    }
}
