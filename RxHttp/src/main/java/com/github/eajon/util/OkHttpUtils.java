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

    private static HttpRequestInterceptor httpRequestInterceptor;

    public static HttpRequestInterceptor getHttpRequestInterceptor() {
        if (httpRequestInterceptor == null) {
            synchronized (OkHttpUtils.class) {
                if (httpRequestInterceptor == null) {
                    httpRequestInterceptor = new HttpRequestInterceptor();
                }
            }
        }
        return httpRequestInterceptor;
    }

    /**
     * 默认httpclient
     */
    private static OkHttpClient httpClient;


    public static OkHttpClient getOkHttpClient() {
        if (httpClient == null) {
            synchronized (OkHttpUtils.class) {
                if (httpClient == null) {
                    httpClient = new OkHttpClient.Builder()
                            .addInterceptor(httpRequestInterceptor)
                            .addNetworkInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                                @Override
                                public void log(String message) {
                                    LoggerUtils.info(message);
                                }
                            })
                                    .setLevel(HttpLoggingInterceptor.Level.BASIC))
                            .build();
                }
            }
        }
        return httpClient;
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
