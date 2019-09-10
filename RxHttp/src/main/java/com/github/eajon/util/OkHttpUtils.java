package com.github.eajon.util;


import com.github.eajon.RxConfig;
import com.github.eajon.interceptor.HttpRequestInterceptor;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author eajon
 */
public class OkHttpUtils {

    private OkHttpUtils() {
        throw new AssertionError();
    }
    /**
     * 内置HttpRequestInterceptor
     */
    private final static HttpRequestInterceptor HTTP_REQUEST_INTERCEPTOR = new HttpRequestInterceptor();

    /**
     * 默认httpclient
     */
    private final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .addInterceptor(HTTP_REQUEST_INTERCEPTOR)
            .addNetworkInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    LoggerUtils.info(message);
                }
            })
                    .setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build();


    public static HttpRequestInterceptor getHttpRequestInterceptor() {
        return HTTP_REQUEST_INTERCEPTOR;
    }
    public static OkHttpClient getOkHttpClient() {
        return HTTP_CLIENT;
    }

    /**
     * 获取OkHttpClient
     * 备注:下载时不能使用OkHttpClient单例,在拦截器中处理进度会导致多任务下载混乱
     *
     * @param interceptorArray
     * @return
     */
    public static OkHttpClient getOkHttpClient(Interceptor... interceptorArray) {
        OkHttpClient.Builder okHttpClientBuilder = RxConfig.get().getOkHttpClient().newBuilder();
        //Interceptor设置
        if (interceptorArray != null) {
            for (Interceptor interceptor : interceptorArray) {
                okHttpClientBuilder.addInterceptor(interceptor);
            }
        }

        return okHttpClientBuilder.build();
    }

}
