package com.github.eajon.function;


import com.github.eajon.RxHttp;
import com.github.eajon.exception.ApiException;
import com.github.eajon.util.LogUtils;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * http结果处理函数
 *
 * @author WENGHYIJIONG
 */
public class ErrorResponseFunction<T> implements Function<Throwable, Observable<T>> {
    @Override
    public Observable<T> apply(@NonNull Throwable throwable) throws Exception {
        //打印具体错误
        LogUtils.e(RxHttp.getConfig().getLogTag() , throwable.getMessage());
        return Observable.error(ApiException.handleException(throwable));
    }
}
