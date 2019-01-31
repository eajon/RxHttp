package com.github.eajon.interceptor;

import com.github.eajon.enums.RequestMethod;
import com.github.eajon.util.NetUtils;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {
    private RequestMethod requestMethod;
    /*请求参数*/
    private Map<String, Object> parameter;
    /*header*/
    private Map<String, Object> header;
    private RequestBody requestBody;

    public RequestInterceptor(RequestMethod requestMethod, Map<String, Object> parameter, Map<String, Object> header, RequestBody requestBody) {
        this.requestMethod = requestMethod;
        this.parameter = parameter;
        this.header = header;
        this.requestBody = requestBody;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request.Builder newRequestBuilder = null;
        switch (requestMethod) {
            case GET:
            case HEAD:
                //添加参数
                HttpUrl.Builder urlBuilder = oldRequest.url().newBuilder();
                if (parameter != null && parameter.keySet().size() > 0) {
                    for (String key : parameter.keySet()) {
                        urlBuilder.addEncodedQueryParameter(key, String.valueOf(parameter.get(key)));
                    }
                }
                newRequestBuilder = oldRequest.newBuilder().get()
                        .method(requestMethod.toString(), null)
                        .url(urlBuilder.build());
                break;
            case POST:
            case PUT:
            case DELETE:
            case PATCH:
                FormBody.Builder formBuilder = new FormBody.Builder();
                if (parameter != null && parameter.keySet().size() > 0) {
                    for (String key : parameter.keySet()) {
                        formBuilder.addEncoded(key, String.valueOf(parameter.get(key)));
                    }
                }
                newRequestBuilder = oldRequest.newBuilder().get()
                        .method(requestMethod.toString(), requestBody != null ? requestBody : formBuilder.build());
                break;

        }


        if (header != null && header.keySet().size() > 0) {
            for (String key : header.keySet()) {
                newRequestBuilder.addHeader(key, NetUtils.getHeaderValueEncoded(header.get(key)).toString());
            }
        }
        return chain.proceed(newRequestBuilder.build());
    }
}
