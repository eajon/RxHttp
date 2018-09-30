package com.github.eajon.observer;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class BaseObserver<T> implements Observer<T> {

    private Disposable disposable;

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
    }


    public void dispose() {
        if (disposable != null&&!disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
