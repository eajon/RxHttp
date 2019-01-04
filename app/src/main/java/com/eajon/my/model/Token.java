package com.eajon.my.model;

public class Token extends BaseResponse {


    /**
     * data : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NDc2NjI1MzMsInVzZXJuYW1lIjoiYWRtaW4ifQ.YFdppE89F6k-rlHtZaMCkqqs5EefSc4LhhYlivb7QcI
     */

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
