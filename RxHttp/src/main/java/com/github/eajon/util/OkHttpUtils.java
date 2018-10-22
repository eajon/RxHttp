package com.github.eajon.util;


import com.github.eajon.RxHttp;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpUtils {


    private static final long TIMEOUT = 60;
    private static HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();



//    /**
//     * 有网时候的缓存
//     */
//    private static Interceptor NetCacheInterceptor = new Interceptor() {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request request = chain.request();
//            Response response = chain.proceed(request);
//            int onlineCacheTime = 30;//在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
//            return response.newBuilder()
//                    .header("Cache-Control", "public, max-age="+onlineCacheTime)
//                    .removeHeader("Pragma")
//                    .build();
//        }
//    };
//
//
//
//
//    /**
//     * 没有网时候的缓存
//     */
//    private static  Interceptor OfflineCacheInterceptor = new Interceptor() {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request request = chain.request();
//            try {
//                if (!NetUtils.isAvailable(RxHttp.getConfig().getContext())) {
//                    int offlineCacheTime = 60;//离线的时候的缓存的过期时间
//                    request = request.newBuilder()
//                            .header("Cache-Control", "public, only-if-cached, max-stale=" + offlineCacheTime)
//                            .build();
//                }
//            }catch (Exception e)
//            {
//
//            }
//
//            return chain.proceed(request);
//        }
//    };

    public static OkHttpClient httpClient = new OkHttpClient.Builder()
//            .addInterceptor(OfflineCacheInterceptor)
//            .addNetworkInterceptor(NetCacheInterceptor)
            .addNetworkInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    LogUtils.e(RxHttp.getConfig().getLogTag(), message);
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
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

        //Interceptor设置
        if (interceptorArray != null) {
            for (Interceptor interceptor : interceptorArray) {
                okHttpClient.addInterceptor(interceptor);
            }
        }
        //超时设置
        okHttpClient.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        return okHttpClient.build();
    }
}
