package com.github.eajon.exception;

/**
 *
 * @author eajon
 */
public class HttpMethodException extends RuntimeException {

    public HttpMethodException() {
        super("method is not allow");
    }
}
