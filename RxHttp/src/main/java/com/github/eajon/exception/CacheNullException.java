package com.github.eajon.exception;

/**
 * http 缓存结果为空处理
 *
 * @author WENGYIJIONG
 **/

public class CacheNullException extends RuntimeException {

    public CacheNullException() {
        super("cache is null");
    }

}
