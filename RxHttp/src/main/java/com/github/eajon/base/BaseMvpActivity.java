package com.github.eajon.base;

import android.os.Bundle;

import com.github.eajon.rxbus.RxIResponse;
import com.github.eajon.rxbus.RxPresenter;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;


public abstract class BaseMvpActivity extends RxAppCompatActivity implements RxIResponse {

    RxPresenter rxPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxPresenter = new RxPresenter(this);
        rxPresenter.attachView(this);
        rxPresenter.observe();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rxPresenter.detachView();
    }
}
