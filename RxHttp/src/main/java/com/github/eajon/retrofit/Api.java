package com.github.eajon.retrofit;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author eajon
 */
public interface Api {

    /**
     * Http请求
     *
     * @param url api接口url
     * @return
     */
    @GET
    Observable<Object> request(@Url String url);


    /**
     * 断点续传下载
     *
     * @param url   下载地址
     * @param range 断点下载范围 bytes= start - end
     * @return
     * @Streaming 防止内容写入内存, 大文件通过此注解避免OOM
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url, @Header("RANGE") String range);

}
