package com.github.eajon.exception;

/**
 * Created By eajon on 2019/9/2.
 */
public class HttpMethodException extends RuntimeException {

    public HttpMethodException() {
        super("method is not allow");
    }
}
