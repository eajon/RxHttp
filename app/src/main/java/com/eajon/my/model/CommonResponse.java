package com.eajon.my.model;

public class CommonResponse<T> extends BaseResponse {

    private T data;

    public T getResult() {
        return data;
    }

    public void setResult(T result) {
        this.data = result;
    }
}
