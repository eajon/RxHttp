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


/**
 * <p>描述：先请求网络，网络请求失败，再加载缓存</p>
 * <p>缓存或网络有一个获取成功就算成功，只有都获取失败才会抛异常</p>
 * <p>最终抛出的是缓存异常，因为只有网络失败才会读缓存，缓存失败就代表两种方式都失败了</p>
 * <p>如果需要知道是否读取了缓存，只需要判断结果回调中的CacheResult的isFromCache为true即可</p>
 * 作者： zhouyou<br>
 * 日期： 2016/12/24 10:35<br>
 * 版本： v2.0<br>
 */
public final class FirstRemoteStrategy extends BaseStrategy {
    @Override
    public <T> Observable<CacheEntity<T>> execute(RxCache rxCache, String key, long time, Observable<T> source, Type type, boolean needCacheCallback) {
        Observable<CacheEntity<T>> cache = loadCache(rxCache, type, key, time, false);
        Observable<CacheEntity<T>> remote = loadRemote(rxCache, key, source, true);
        return remote.switchIfEmpty(cache);
    }
}
