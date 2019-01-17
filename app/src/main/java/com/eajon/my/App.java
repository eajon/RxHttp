package com.eajon.my;

import android.app.Application;
import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.github.eajon.RxHttp;
import com.github.eajon.interceptor.HttpLoggingInterceptor;
import com.github.eajon.util.LogUtils;
import com.github.eajon.util.NetUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Interceptor netCacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                int onlineCacheTime = 30;//在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
                return response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + onlineCacheTime)
                        .removeHeader("Pragma")
                        .build();
            }
        };

        Interceptor offlineCacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                try {
                    if (!NetUtils.isAvailable(getContext())) {
                        int offlineCacheTime = 30;//离线的时候的缓存的过期时间
                        request = request.newBuilder()
                                .header("Cache-Control", "public, only-if-cached, max-stale=" + offlineCacheTime)
                                .build();
                    }
                } catch (Exception e) {

                }

                return chain.proceed(request);
            }
        };

        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getContext()));

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(offlineCacheInterceptor)
                .addNetworkInterceptor(netCacheInterceptor)
                .addNetworkInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        LogUtils.e(message);
                    }
                })
                        .setLevel(HttpLoggingInterceptor.Level.BASIC))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                .build();


        RxHttp
                .getConfig()
                .baseUrl("http://172.17.12.42:8088/")
                .okHttpClient(httpClient)
                .rxCache(new File(getExternalCacheDir(), "rxcache"));

        LogUtils.init(this, "eajon", true, false);
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
