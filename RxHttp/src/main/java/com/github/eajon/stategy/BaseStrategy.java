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
import com.github.eajon.util.LoggerUtils;

import java.lang.reflect.Type;
import java.util.ConcurrentModificationException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * @author eajon
 */
public abstract class BaseStrategy implements IStrategy {

    <T> Observable<CacheEntity<T>> loadCache(final RxCache rxCache, Type type, final String key, final long time, final boolean needEmpty) {
        Observable<CacheEntity<T>> observable = rxCache.<T>load(key, type, time).flatMap(new Function<T, ObservableSource<CacheEntity<T>>>() {
            @Override
            public ObservableSource<CacheEntity<T>> apply(@NonNull T t) throws Exception {
                return Observable.just(new CacheEntity<>(true, t));
            }
        });
        if (needEmpty) {
            observable = observable
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CacheEntity<T>>>() {
                        @Override
                        public ObservableSource<? extends CacheEntity<T>> apply(@NonNull Throwable throwable) throws Exception {
                            return Observable.empty();
                        }
                    });
        }
        return observable;
    }

    //请求成功后：异步保存
    <T> Observable<CacheEntity<T>> loadRemote2(final RxCache rxCache, final String key, Observable<T> source, final boolean needEmpty) {
        Observable<CacheEntity<T>> observable = source
                .map(new Function<T, CacheEntity<T>>() {
                    @Override
                    public CacheEntity<T> apply(@NonNull T t) throws Exception {
                        LoggerUtils.info("loadRemote result=" + t);
                        rxCache.save(key, t).subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(@NonNull Boolean status) throws Exception {
                                        LoggerUtils.info("save status => " + status);
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception {
                                        if (throwable instanceof ConcurrentModificationException) {
                                            LoggerUtils.info("Save failed, please use a synchronized cache strategy :", throwable);
                                        } else {
                                            LoggerUtils.info(throwable.getMessage());
                                        }
                                    }
                                });
                        return new CacheEntity<T>(false, t);
                    }
                });
        if (needEmpty) {
            observable = observable
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CacheEntity<T>>>() {
                        @Override
                        public ObservableSource<? extends CacheEntity<T>> apply(@NonNull Throwable throwable) throws Exception {
                            return Observable.empty();
                        }
                    });
        }
        return observable;
    }

    //请求成功后：同步保存
    <T> Observable<CacheEntity<T>> loadRemote(final RxCache rxCache, final String key, Observable<T> source, final boolean needEmpty) {
        Observable<CacheEntity<T>> observable = source
                .flatMap(new Function<T, ObservableSource<CacheEntity<T>>>() {
                    @Override
                    public ObservableSource<CacheEntity<T>> apply(final @NonNull T t) throws Exception {
                        return rxCache.save(key, t).map(new Function<Boolean, CacheEntity<T>>() {
                            @Override
                            public CacheEntity<T> apply(@NonNull Boolean aBoolean) throws Exception {
                                LoggerUtils.info("save status => " + aBoolean);
                                return new CacheEntity<T>(false, t);
                            }
                        }).onErrorReturn(new Function<Throwable, CacheEntity<T>>() {
                            @Override
                            public CacheEntity<T> apply(@NonNull Throwable throwable) throws Exception {
                                LoggerUtils.info("save status => " + throwable);
                                return new CacheEntity<T>(false, t);
                            }
                        });
                    }
                });
        if (needEmpty) {
            observable = observable
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CacheEntity<T>>>() {
                        @Override
                        public ObservableSource<? extends CacheEntity<T>> apply(@NonNull Throwable throwable) throws Exception {
                            return Observable.empty();
                        }
                    });
        }
        return observable;
    }
}
