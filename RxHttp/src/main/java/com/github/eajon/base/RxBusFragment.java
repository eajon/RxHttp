package com.github.eajon.base;

import android.os.Bundle;
import com.threshold.rxbus2.RxBus;
import com.trello.rxlifecycle2.components.support.RxFragment;

public abstract class RxBusFragment extends RxFragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RxBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(this);
    }
}
