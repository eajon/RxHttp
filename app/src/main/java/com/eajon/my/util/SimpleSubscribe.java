package com.eajon.my.util;

import com.github.eajon.util.LoggerUtils;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;


public abstract class SimpleSubscribe<T> implements ObservableOnSubscribe<T> {
    @Override
    public void subscribe(@NonNull ObservableEmitter<T> subscriber) {
        try {
            T data = load();
            if (!subscriber.isDisposed()) {
                if (data != null) {
                    subscriber.onNext(data);
                } else {
                    subscriber.onError(new NullPointerException());
                }
            }
        } catch (Throwable e) {
            LoggerUtils.info(e.getMessage());
            if (!subscriber.isDisposed()) {
                subscriber.onError(e);
            }
            Exceptions.throwIfFatal(e);
            RxJavaPlugins.onError(e);
            return;
        }
        if (!subscriber.isDisposed()) {
            subscriber.onComplete();
        }
    }

    public abstract T load();
}