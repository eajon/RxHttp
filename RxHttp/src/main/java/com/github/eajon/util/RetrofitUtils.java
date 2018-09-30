package com.github.eajon.util;

import com.github.eajon.RxHttp;
import com.github.eajon.retrofit.Api;

import okhttp3.Interceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit工具类
 * 获取Retrofit 默认使用OkHttpClient
 *
 * @author wengyijiong
 */
public class RetrofitUtils {

    private static RetrofitUtils instance = null;
    private static Retrofit.Builder retrofit;


    private RetrofitUtils() {
        retrofit = new Retrofit.Builder();
    }

    public static RetrofitUtils get() {
        if (instance == null) {
            synchronized (RetrofitUtils.class) {
                if (instance == null) {
                    instance = new RetrofitUtils();
                }
            }
        }
        return instance;
    }


    /**
     * 获取ApiService
     *
     * @param baseUrl
     * @return
     */
    public Api getRetrofit(String baseUrl) {
        retrofit
                .client(RxHttp.getConfig().getOkHttpClient())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return retrofit.build().create(Api.class);
    }


    /**
     * 获取ApiService,自定义拦截器,用于下载
     *
     * @param baseUrl
     * @return
     */
    public Api getRetrofit(String baseUrl, Interceptor... interceptorArray) {
        retrofit
                .client(OkHttpUtils.getOkHttpClient(interceptorArray))
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return retrofit.build().create(Api.class);
    }


}
