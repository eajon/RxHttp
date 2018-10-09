package com.github.eajon.observer;


import com.github.eajon.RxHttp;
import com.github.eajon.util.LogUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class BaseObserver<T> implements Observer <T> {


    public abstract void onSuccess(T t);

    public abstract void onError(String t);

    private Disposable disposable;

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
    }


    public void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onNext(T value) {
        LogUtils.e(RxHttp.getConfig().getLogTag(), "success:" + value.toString());
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
}
