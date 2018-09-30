package com.github.eajon.retrofit;

import android.content.Context;

import com.github.eajon.util.LogUtils;
import com.github.eajon.util.OkHttpUtils;

import java.util.Map;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;

public class RxConfig {


    private static RxConfig config;

    public static RxConfig init(Context context) {
        if (config == null) {
            synchronized (RxConfig.class) {
                if (config == null) {
                    config = new RxConfig(context);
                    setRxJava2ErrorHandler();
                }
            }
        }
        return config;
    }

    public static RxConfig get() {
        return config;
    }


    private static void setRxJava2ErrorHandler() {
        RxJavaPlugins.setErrorHandler(new Consumer <Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.e(config.getLogTag(), throwable);
            }
        });

    }

    Context context;

    /*请求基础路径*/
    String baseUrl;
    /*请求参数*/
    Map <String, Object> parameter;
    /*header*/
    Map <String, Object> header;

    OkHttpClient okHttpClient;

    String logTag = "RxHttp";


    private RxConfig(Context context) {
        this.context = context;
    }


    public Context getContext() {
        return context;
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

    public Map <String, Object> getBaseParameter() {
        return parameter;
    }

    /*基础Header*/
    public RxConfig baseHeader(Map <String, Object> header) {
        this.header = header;
        return this;
    }

    public Map <String, Object> getBaseHeader() {
        return header;
    }

    /*logTag*/
    public RxConfig logTag(String logTag) {
        this.logTag = logTag;
        return this;
    }

    public String getLogTag() {
        return logTag;
    }


    /*HttpClient*/
    public RxConfig okHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        return this;
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            return OkHttpUtils.httpClient;
        }
        return okHttpClient;
    }


}
