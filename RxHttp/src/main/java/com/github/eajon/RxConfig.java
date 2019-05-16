package com.github.eajon;

import com.github.eajon.cache.RxCache;
import com.github.eajon.enums.CacheMode;
import com.github.eajon.util.GsonUtils;
import com.github.eajon.util.LoggerUtils;
import com.github.eajon.util.OkHttpUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.io.File;
import java.util.Map;

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
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
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
    Map<String, Object> parameter;
    /*header*/
    Map<String, Object> header;

    OkHttpClient okHttpClient;

    RxCache rxCache;

    CacheMode cacheMode;

    String logTag = "RxHttp";


    private RxConfig() {
        setRxJava2ErrorHandler();
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
    public RxConfig baseParameter(Map<String, Object> parameter) {
        this.parameter = parameter;
        return this;
    }

    /*基础参数*/
    public RxConfig baseTypeParameter(Object object) {
        this.parameter = GsonUtils.objectToMap(object);
        return this;
    }

    public Map<String, Object> getBaseParameter() {
        return parameter;
    }

    /*基础Header*/
    public RxConfig baseHeader(Map<String, Object> header) {
        this.header = header;
        return this;
    }

    /*基础Header*/
    public RxConfig baseTypeHeader(Object object) {
        this.header = GsonUtils.objectToMap(object);
        return this;
    }

    public Map<String, Object> getBaseHeader() {
        return header;
    }


    /*HttpClient*/
    public RxConfig okHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        return this;
    }

    public RxCache getRxCache() {
        return rxCache;
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public OkHttpClient getOkHttpClient() {
        return this.okHttpClient == null ? OkHttpUtils.HttpClient : this.okHttpClient;
    }

    public RxConfig rxCache(File cacheDir) {
        this.rxCache = new RxCache.Builder()
                .cacheDir(cacheDir)
                .build();
        this.cacheMode = CacheMode.FIRSTREMOTE;
        return this;
    }


    public RxConfig rxCache(File cacheDir, CacheMode cacheMode) {
        rxCache = new RxCache.Builder()
                .cacheDir(cacheDir)
                .build();
        this.cacheMode = cacheMode;
        return this;
    }

    public RxConfig rxCache(File cacheDir, CacheMode cacheMode, long cacheMaxSize, long cacheExpTime, int cacheVersion) {
        rxCache = new RxCache.Builder()
                .cacheDir(cacheDir)
                .cacheMaxSize(cacheMaxSize)
                .cacheExpTime(cacheExpTime)
                .cacheVersion(cacheVersion)
                .build();
        this.cacheMode = cacheMode;
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
