package com.github.eajon.exception;

/**
 * http 缓存结果为空处理
 *
 * @author WENGYIJIONG
 *
 **/

public class RxCacheNullException extends RuntimeException {

    public RxCacheNullException() {
        super("cache is null");
    }

}
