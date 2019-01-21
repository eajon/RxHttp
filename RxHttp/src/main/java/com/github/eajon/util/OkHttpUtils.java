package com.github.eajon.util;


import com.github.eajon.RxConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpUtils {


    private static final long TIMEOUT = 30;
    private static HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();

    //默认HttpClient
    public static OkHttpClient HttpClient = new OkHttpClient.Builder()
            .addNetworkInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    LoggerUtils.info(message);
                }
            })
                    .setLevel(HttpLoggingInterceptor.Level.BASIC))
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            .build();

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
