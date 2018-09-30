package com.github.eajon.rxbus;

public class RxResponse<T> {

    private String tag;
    private T data;

    public RxResponse(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
