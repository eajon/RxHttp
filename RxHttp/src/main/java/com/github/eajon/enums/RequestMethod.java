package com.github.eajon.enums;

/**
 * Http请求方式
 *
 * @author WENGYIJIONG
 */
public enum RequestMethod {
    /*GET*/
    GET("GET"),
    /*POST*/
    POST("POST"),
    /*DELETE*/
    DELETE("DELETE"),
    /*PUT*/
    PUT("PUT"),
    /*PATCH*/
    PATCH("PATCH"),
    /*HEAD*/
    HEAD("HEAD");

    RequestMethod(String method) {
    }

}
