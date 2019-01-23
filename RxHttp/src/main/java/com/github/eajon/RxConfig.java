package com.github.eajon;

import com.github.eajon.cache.RxCacheProvider;
import com.github.eajon.model.CacheMode;
import com.github.eajon.util.GsonUtils;
import com.github.eajon.util.LoggerUtils;
import com.github.eajon.util.OkHttpUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.threshold.rxbus2.RxBus;

import java.io.File;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;

public class RxConfig {


    private volatile static RxConfig config;

    public static RxConfig get() {
        if (config == null) {
            synchronized (RxConfig.class) {
                if (config == null) {
                    config = new RxConfig();
                }
            }
        }
        return config;
    }


    private void setRxJava2ErrorHandler() {
        RxJavaPlugins.setErrorHandler(new Consumer <Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LoggerUtils.error(throwable, throwable.getMessage());
            }
        });

    }

    private void addLogAdapter() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag(logTag)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }


    /*请求基础路径*/
    String baseUrl;
    /*请求参数*/
    Map <String, Object> parameter;
    /*header*/
    Map <String, Object> header;

    OkHttpClient okHttpClient;

    String logTag = "RxHttp";


    private RxConfig() {
        setRxJava2ErrorHandler();
        RxBus.setMainScheduler(AndroidSchedulers.mainThread());
        addLogAdapter();
    }


    /*请求基础路径*/
    public RxConfig baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /*基础参数*/
    public RxConfig baseParameter(Map <String, Object> parameter) {
        this.parameter = parameter;
        return this;
    }

    /*基础参数*/
    public RxConfig baseObjectParameter(Object object) {
        this.parameter = GsonUtils.objectToMap(object);
        return this;
    }

    public Map <String, Object> getBaseParameter() {
        return parameter;
    }

    /*基础Header*/
    public RxConfig baseHeader(Map <String, Object> header) {
        this.header = header;
        return this;
    }

    /*基础Header*/
    public RxConfig baseObjectHeader(Object object) {
        this.header = GsonUtils.objectToMap(object);
        return this;
    }

    public Map <String, Object> getBaseHeader() {
        return header;
    }


    /*HttpClient*/
    public RxConfig okHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        return this;
    }



    public OkHttpClient getOkHttpClient() {
        return this.okHttpClient == null ? OkHttpUtils.HttpClient : this.okHttpClient;
    }

    public RxConfig rxCache(File cacheDir) {
        RxCacheProvider.getInstance()
                .setCacheDirectory(cacheDir)
                .setCacheMode(CacheMode.FIRSTREMOTE)
                .setCacheMaxSize(50 * 1024 * 1024)
                .setCacheTime(-1)
                .setCacheVersion(1);
        return this;
    }


    public RxConfig rxCache(File cacheDir, CacheMode cacheMode) {
        RxCacheProvider.getInstance()
                .setCacheDirectory(cacheDir)
                .setCacheMode(cacheMode)
                .setCacheMaxSize(50 * 1024 * 1024)
                .setCacheTime(-1)
                .setCacheVersion(1);
        return this;
    }

    public RxConfig rxCache(File cacheDir, CacheMode cacheMode, long cacheMaxSize, long cacheExpTime, int cacheVersion) {
        RxCacheProvider.getInstance()
                .setCacheDirectory(cacheDir)
                .setCacheMaxSize(cacheMaxSize)
                .setCacheTime(cacheExpTime)
                .setCacheMode(cacheMode)
                .setCacheVersion(cacheVersion);
        return this;
    }

    public RxConfig log(boolean isDebug, String logTag) {
        LoggerUtils.init(isDebug);
        this.logTag = logTag;
        Logger.clearLogAdapters();
        addLogAdapter();
        return this;
    }


}
