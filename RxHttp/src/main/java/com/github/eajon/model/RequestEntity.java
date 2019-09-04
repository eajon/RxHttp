package com.github.eajon.model;

import com.github.eajon.enums.RequestMethod;

import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created By eajon on 2019/9/2.
 */
public class RequestEntity {

    private RequestMethod requestMethod;
    /*请求参数*/
    private Map<String, Object> parameter;
    /*header*/
    private Map<String, Object> header;
    private RequestBody requestBody;

    public RequestEntity(RequestMethod requestMethod, Map<String, Object> parameter, Map<String, Object> header, RequestBody requestBody) {
        this.requestMethod = requestMethod;
        this.parameter = parameter;
        this.header = header;
        this.requestBody = requestBody;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Map<String, Object> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, Object> parameter) {
        this.parameter = parameter;
    }

    public Map<String, Object> getHeader() {
        return header;
    }

    public void setHeader(Map<String, Object> header) {
        this.header = header;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }
}
