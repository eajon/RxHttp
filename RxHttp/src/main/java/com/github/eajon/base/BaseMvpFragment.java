package com.github.eajon.base;

import android.os.Bundle;


import com.github.eajon.rxbus.RxIResponse;
import com.github.eajon.rxbus.RxPresenter;
import com.trello.rxlifecycle2.components.support.RxFragment;

public abstract class BaseMvpFragment extends RxFragment implements RxIResponse {

    RxPresenter rxPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxPresenter = new RxPresenter(this);
        rxPresenter.attachView(this);
        rxPresenter.observe();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rxPresenter.detachView();
    }
}
