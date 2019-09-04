package com.github.eajon.interceptor;

import com.github.eajon.enums.RequestMethod;
import com.github.eajon.exception.HttpMethodException;
import com.github.eajon.model.RequestEntity;
import com.github.eajon.util.NetUtils;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created By eajon on 2019/9/2.
 */
public class HttpRequestInterceptor implements Interceptor {

    private RequestEntity requestEntity;


    public HttpRequestInterceptor() {
    }


    public void setRequestEntity(RequestEntity requestEntity) {
        this.requestEntity = requestEntity;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request.Builder newRequestBuilder;
        HttpUrl.Builder urlBuilder = oldRequest.url().newBuilder();
        RequestMethod requestMethod = requestEntity.getRequestMethod();
        Map<String, Object> parameter = requestEntity.getParameter();
        Map<String, Object> header = requestEntity.getHeader();
        RequestBody requestBody = requestEntity.getRequestBody();
        switch (requestMethod) {
            case GET:
            case HEAD:
            case DELETE:
                //添加参数
                if (parameter != null && parameter.keySet().size() > 0) {
                    for (String key : parameter.keySet()) {
                        urlBuilder.addEncodedQueryParameter(key, String.valueOf(parameter.get(key)));
                    }
                }
                break;
            case POST:
            case PUT:
            case PATCH:
                if (requestBody != null) {
                    if (parameter != null && parameter.keySet().size() > 0) {
                        for (String key : parameter.keySet()) {
                            urlBuilder.addEncodedQueryParameter(key, String.valueOf(parameter.get(key)));
                        }
                    }
                } else {
                    FormBody.Builder formBuilder = new FormBody.Builder();
                    if (parameter != null && parameter.keySet().size() > 0) {
                        for (String key : parameter.keySet()) {
                            formBuilder.addEncoded(key, String.valueOf(parameter.get(key)));
                        }
                    }
                    requestBody = formBuilder.build();
                }
                break;
            default:
                throw new HttpMethodException();
        }
        newRequestBuilder = oldRequest.newBuilder().method(requestMethod.name(), requestBody)
                .url(urlBuilder.build());
        if (header != null && header.keySet().size() > 0) {
            for (String key : header.keySet()) {
                newRequestBuilder.addHeader(key, NetUtils.getHeaderValueEncoded(header.get(key)).toString());
            }
        }
        return chain.proceed(newRequestBuilder.build());
    }
}
