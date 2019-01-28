package com.github.eajon.base;

import android.os.Bundle;

import com.github.eajon.rxbus.RxBus;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;


public abstract class RxBusActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(this);
    }
}
