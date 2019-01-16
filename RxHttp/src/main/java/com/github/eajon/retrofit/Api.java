package com.github.eajon.retrofit;

import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface Api {

    /**
     * GET 请求
     *
     * @param url       api接口url
     * @param parameter 请求参数map
     * @param header    请求头map
     * @return
     */
    @GET
    Observable<JsonElement> get(@Url String url, @QueryMap Map <String, Object> parameter, @HeaderMap Map <String, Object> header);



    /**
     * POST 请求
     *
     * @param url       api接口url
     * @param parameter 请求参数map
     * @param header    请求头map
     * @return
     */
    @FormUrlEncoded
    @POST
    Observable<JsonElement> post(@Url String url, @FieldMap Map <String, Object> parameter, @HeaderMap Map <String, Object> header);



    /**
     * JSON POST 请求
     *
     * @param url       api接口url
     * @param info  请求参数JSON
     * @return
     */
    @POST
    Observable <JsonElement> post(@Url String url, @Body RequestBody info, @HeaderMap Map <String, Object> header);

    /**
     * DELETE 请求
     *
     * @param url       api接口url
     * @param parameter 请求参数map
     * @param header    请求头map
     * @return
     */
    @FormUrlEncoded
    @DELETE
    Observable<JsonElement> delete(@Url String url, @FieldMap Map <String, Object> parameter, @HeaderMap Map <String, Object> header);


    /**
     * PUT 请求
     *
     * @param url       api接口url
     * @param parameter 请求参数map
     * @param header    请求头map
     * @return
     */
    @FormUrlEncoded
    @PUT
    Observable<JsonElement> put(@Url String url, @FieldMap Map <String, Object> parameter, @HeaderMap Map <String, Object> header);




    /**
     * 单文件上传
     *
     * @param url       api接口url
     * @param parameter 请求接口参数
     * @param header    请求头map
     * @param file  文件列表
     * @return
     * @Multipart 文件上传注解
     */
    @Multipart
    @POST
    Observable<JsonElement> upload(@Url String url, @PartMap Map<String, RequestBody> parameter, @HeaderMap Map <String, Object> header,  @Part MultipartBody.Part file);

    /**
     * 多文件上传
     *
     * @param url       api接口url
     * @param parameter 请求接口参数
     * @param header    请求头map
     * @param fileList  文件列表
     * @return
     * @Multipart 文件上传注解
     */
    @Multipart
    @POST
    Observable<JsonElement> upload(@Url String url, @PartMap Map<String, RequestBody> parameter, @HeaderMap Map <String, Object> header, @Part List <MultipartBody.Part> fileList);




    /**
     * 断点续传下载
     *
     *
     * @param url   下载地址
     * @param range 断点下载范围 bytes= start - end
     * @return
     * @Streaming 防止内容写入内存, 大文件通过此注解避免OOM
     */
    @Streaming
    @GET
    Observable<ResponseBody> getDownload(@Url String url, @Header("RANGE") String range, @QueryMap Map<String, Object> parameter, @HeaderMap Map<String, Object> header);

    @Streaming
    @POST
    Observable<ResponseBody> postDownload(@Url String url, @Header("RANGE") String range, @FieldMap Map<String, Object> parameter, @HeaderMap Map<String, Object> header);
}
