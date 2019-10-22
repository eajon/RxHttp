package com.github.eajon.util;

import com.github.eajon.RxConfig;
import com.github.eajon.RxHttp;
import com.github.eajon.interceptor.HttpRequestInterceptor;
import com.github.eajon.model.RequestEntity;
import com.github.eajon.retrofit.Api;

import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Retrofit工具类
 * 获取Retrofit 默认使用OkHttpClient
 *
 * @author wengyijiong
 */
public class RetrofitUtils {

    private RetrofitUtils() {
        throw new AssertionError();
    }

    /**
     * 获取ApiService
     *
     * @param baseUrl
     * @return
     */
    public static Api getRetrofit(String baseUrl, RequestEntity requestEntity) {
        OkHttpClient okHttpClient = RxHttp.getConfig().getOkHttpClient();
        List<Interceptor> interceptorList = okHttpClient.interceptors();
        for (int i = 0; i < interceptorList.size(); i++) {
            if (interceptorList.get(i) instanceof HttpRequestInterceptor) {
                (( HttpRequestInterceptor ) interceptorList.get(i)).setRequestEntity(requestEntity);
            }
        }
        Retrofit.Builder builder = new Retrofit.Builder().client(okHttpClient)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        switch (RxConfig.get().getConverterType()) {
            case JACKSON:
                builder.addConverterFactory(JacksonConverterFactory.create());
                break;
            case FASTJSON:
                builder.addConverterFactory(FastJsonConverterFactory.create());
                break;
            case GSON:
            default:
                builder.addConverterFactory(GsonConverterFactory.create());
                break;
        }
        return builder.build().create(Api.class);
    }


    /**
     * 获取ApiService,自定义拦截器,用于下载
     *
     * @param baseUrl
     * @return
     */
    public static Api getRetrofit(String baseUrl, Interceptor... interceptorArray) {
        Retrofit.Builder builder = new Retrofit.Builder().client(OkHttpUtils.getOkHttpClient(interceptorArray))
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        switch (RxConfig.get().getConverterType()) {
            case JACKSON:
                builder.addConverterFactory(JacksonConverterFactory.create());
                break;
            case FASTJSON:
                builder.addConverterFactory(FastJsonConverterFactory.create());
                break;
            case GSON:
            default:
                builder.addConverterFactory(GsonConverterFactory.create());
                break;
        }
        return builder.build().create(Api.class);
    }


}
