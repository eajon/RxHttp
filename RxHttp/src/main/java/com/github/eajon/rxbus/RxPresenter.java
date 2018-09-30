package com.github.eajon.rxbus;


import com.trello.rxlifecycle2.LifecycleProvider;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class RxPresenter {

    private Disposable disposable;
    private RxIResponse RxIResponse;
    private LifecycleProvider lifecycleProvider;

    public RxPresenter(LifecycleProvider lifecycleProvider) {
        this.lifecycleProvider = lifecycleProvider;
    }

    public void attachView(RxIResponse rxIMessage) {
        this.RxIResponse = rxIMessage;
    }

    public void observe() {
        disposable = RxBusRelay.get().registerIOMain(RxResponse.class).compose(lifecycleProvider.bindToLifecycle())
                .subscribe(new Consumer <RxResponse>() {
                    @Override
                    public void accept(RxResponse response) throws Exception {
                        RxIResponse.onResponse(response);
                    }
                });

    }

    public void detachView() {
        if (null!=disposable) {
            disposable.dispose();
            disposable = null;
        }
    }


}
