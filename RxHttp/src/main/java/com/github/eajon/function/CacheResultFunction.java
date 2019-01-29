package com.github.eajon.function;


import com.github.eajon.model.CacheResult;
import com.github.eajon.util.LoggerUtils;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * <p>描述：缓存结果转换</p>
 * 作者： zhouyou<br>
 * 日期： 2017/4/21 10:53 <br>
 * 版本： v1.0<br>
 */
public class CacheResultFunction<T> implements Function<CacheResult<T>, T> {
    @Override
    public T apply(@NonNull CacheResult<T> cacheResult) {
        LoggerUtils.info("isFormCache: %s ,data: %s", cacheResult.isFromCache(), cacheResult.getData());
        return cacheResult.getData();
    }
}
