package com.eajon.my;

import android.app.Application;
import android.content.Context;

import com.github.eajon.retrofit.RxConfig;

public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        RxConfig.init(this).logTag("RxHttps");


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    //获取application
    public static App getContext() {
        return instance;
    }
}
