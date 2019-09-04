package com.github.eajon.function;


import com.github.eajon.model.CacheEntity;
import com.github.eajon.util.LoggerUtils;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * @author eajon
 */
public class CacheResultFunction<T> implements Function<CacheEntity<T>, T> {
    @Override
    public T apply(@NonNull CacheEntity<T> cacheEntity) {
        LoggerUtils.info("isFormCache: %s ,data: %s", cacheEntity.isFromCache(), cacheEntity.getData());
        return cacheEntity.getData();
    }
}
