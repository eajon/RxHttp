package com.github.eajon.function;


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
public class ErrorResponseFunction<T> implements Function <Throwable, Observable> {
    @Override
    public Observable <? extends T> apply(@NonNull Throwable throwable)  {
        //打印具体错误
        ApiException apiException =ApiException.handleException(throwable);
        LogUtils.e(apiException.getDisplayMessage());
        return Observable.error(apiException);
    }
}
