package com.eajon.my;

import android.app.Application;
import android.content.Context;

import com.github.eajon.converter.GsonDiskConverter;
import com.github.eajon.retrofit.RxConfig;

import java.io.File;

import static com.github.eajon.model.CacheMode.CACHEANDREMOTEDISTINCT;
import static com.github.eajon.model.CacheMode.FIRSTCACHE;

public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        RxConfig.get().logTag("RxHttps");
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
