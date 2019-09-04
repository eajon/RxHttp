/*
 * Copyright (C) 2017 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.eajon.stategy;


import com.github.eajon.cache.RxCache;
import com.github.eajon.model.CacheEntity;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import okio.ByteString;

/**
 * @author eajon
 */
public final class CacheAndRemoteDistinctStrategy extends BaseStrategy {
    @Override
    public <T> Observable<CacheEntity<T>> execute(RxCache rxCache, String key, long time, Observable<T> source, Type type, boolean need) {
        Observable<CacheEntity<T>> cache = loadCache(rxCache, type, key, time, true);
        Observable<CacheEntity<T>> remote = loadRemote(rxCache, key, source, false);
        return Observable.concat(cache, remote)
                .filter(new Predicate<CacheEntity<T>>() {
                    @Override
                    public boolean test(@NonNull CacheEntity<T> tCacheEntity) throws Exception {
                        return tCacheEntity != null && tCacheEntity.getData() != null;
                    }
                }).distinctUntilChanged(new Function<CacheEntity<T>, String>() {
                    @Override
                    public String apply(@NonNull CacheEntity<T> tCacheEntity) throws Exception {
                        return ByteString.of(tCacheEntity.getData().toString().getBytes()).md5().hex();
                    }
                });
    }

}
