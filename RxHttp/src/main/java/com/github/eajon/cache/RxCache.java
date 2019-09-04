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

package com.github.eajon.cache;

import android.os.StatFs;
import android.util.Log;

import com.github.eajon.RxConfig;
import com.github.eajon.converter.FastJsonDiskConvert;
import com.github.eajon.converter.GsonDiskConverter;
import com.github.eajon.converter.IDiskConverter;
import com.github.eajon.converter.JacksonDiskConverter;
import com.github.eajon.enums.CacheMode;
import com.github.eajon.exception.CacheNullException;
import com.github.eajon.model.CacheEntity;
import com.github.eajon.model.RealEntity;
import com.github.eajon.stategy.CacheAndRemoteDistinctStrategy;
import com.github.eajon.stategy.CacheAndRemoteStrategy;
import com.github.eajon.stategy.FirstCacheStrategy;
import com.github.eajon.stategy.FirstRemoteStrategy;
import com.github.eajon.stategy.IStrategy;
import com.github.eajon.stategy.NoStrategy;
import com.github.eajon.stategy.OnlyCacheStrategy;
import com.github.eajon.stategy.OnlyRemoteStrategy;

import java.io.File;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;


/**
 * <p>描述：缓存统一入口类</p>
 * <p>
 * <p>主要实现技术：Rxjava+DiskLruCache(jakewharton大神开源的LRU库)</p>
 * <p>
 * <p>
 * 主要功能：<br>
 * 1.可以独立使用，单独用RxCache来存储数据<br>
 * 2.采用transformer与网络请求结合，可以实现网络缓存功能,本地硬缓存<br>
 * 3.可以保存缓存 （异步）<br>
 * 4.可以读取缓存（异步）<br>
 * 5.可以判断缓存是否存在<br>
 * 6.根据key删除缓存<br>
 * 7.清空缓存（异步）<br>
 * 8.缓存Key会自动进行MD5加密<br>
 * 9.其它参数设置：缓存磁盘大小、缓存key、缓存时间、缓存存储的转换器、缓存目录、缓存Version<br>
 * <p>
 * <p>
 * 使用说明：<br>
 * RxCache rxCache = new RxCache.Builder(this)<br>
 * .cacheVersion(1)//不设置，默认为1</br>
 * .diskDir(new File(getCacheDir().getPath() + File.separator + "data-cache"))//不设置，默认使用缓存路径<br>
 * .diskConverter(new SerializableDiskConverter())//目前只支持Serializable缓存<br>
 * .diskMax(20*1024*1024)//不设置， 默为认50MB<br>
 * .build();</br>
 * </P>
 * 作者： zhouyou<br>
 * 日期： 2016/12/24 10:35<br>
 * 版本： v2.0<br>
 */
public final class RxCache {

    private static final String TAG = "RxCache";
    private final CacheCore cacheCore;                                  //缓存的核心管理类
    private final long cacheTime;                                       //缓存的时间 单位:秒
    private final int cacheVersion;                                     //缓存的版本
    private final IDiskConverter diskConverter;                         //缓存的转换器
    private final File diskDir;                                         //缓存的磁盘目录，默认是缓存目录
    private final long diskMaxSize;                                     //缓存的磁盘大小

    public RxCache() {
        this(new Builder());
    }

    private RxCache(Builder builder) {
        this.cacheTime = builder.cacheTime;
        this.diskDir = builder.diskDir;
        this.cacheVersion = builder.cacheVersion;
        this.diskMaxSize = builder.diskMaxSize;
        this.diskConverter = builder.diskConverter;
        cacheCore = new CacheCore(new LruDiskCache(diskConverter, diskDir, cacheVersion, diskMaxSize));
    }

    public Builder newBuilder() {
        return new Builder(this);
    }


    @SuppressWarnings(value = {"unchecked", "deprecation"})
    public <T> ObservableTransformer<T, CacheEntity<T>> transformer(CacheMode cacheMode, Type type, String cacheKey) {
        return transformer(cacheMode, type, false, cacheKey);
    }

    /**
     * 缓存transformer
     *
     * @param cacheMode 缓存类型
     * @param type      缓存clazz
     */
    @SuppressWarnings(value = {"unchecked", "deprecation"})
    public <T> ObservableTransformer<T, CacheEntity<T>> transformer(CacheMode cacheMode, final Type type, final boolean needCacheCallback, final String cacheKey) {
        final IStrategy strategy = loadStrategy(cacheMode);//获取缓存策略
        return new ObservableTransformer<T, CacheEntity<T>>() {
            @Override
            public ObservableSource<CacheEntity<T>> apply(@NonNull Observable<T> upstream) {
                Log.i(TAG, "cackeKey=" + cacheKey);
                return strategy.execute(RxCache.this, cacheKey, RxCache.this.cacheTime, upstream, type, needCacheCallback);
            }
        };
    }

    private static abstract class SimpleSubscribe<T> implements ObservableOnSubscribe<T> {
        @Override
        public void subscribe(@NonNull ObservableEmitter<T> subscriber) throws Exception {
            try {
                T data = execute();
                if (!subscriber.isDisposed()) {
                    if (data instanceof RealEntity) {
                        if ((( RealEntity ) data).getData() != null) {
                            subscriber.onNext(data);
                        } else {
                            subscriber.onError(new CacheNullException());
                        }
                    } else {
                        if (data != null) {
                            subscriber.onNext(data);
                        } else {
                            subscriber.onError(new CacheNullException());
                        }
                    }
                }
            } catch (Throwable e) {
                Log.e(TAG, e.getMessage());
                if (!subscriber.isDisposed()) {
                    subscriber.onError(e);
                }
                Exceptions.throwIfFatal(e);
                //RxJavaPlugins.onError(e);
                return;
            }

            if (!subscriber.isDisposed()) {
                subscriber.onComplete();
            }
        }

        abstract T execute() throws Throwable;
    }

    /**
     * 同步获取缓存
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        return get(key, ( Type ) clazz);
    }


    /**
     * 同步获取缓存
     *
     * @param key
     * @param type
     * @param <T>
     * @return
     */
    public <T> T get(String key, Type type) {
        RealEntity<T> result = cacheCore.load(type, key, -1);
        if (result != null) {
            return result.getData();
        }
        return null;
    }

    /**
     * 通过Rx的方式获取缓存，返回一个Observable
     *
     * @param key   缓存key
     * @param clazz 保存的类型
     * @param <T>
     * @return
     */
    public <T> Observable<T> load(String key, Class<T> clazz) {
        return load(key, ( Type ) clazz);
    }


    /**
     * 通过Rx的方式获取缓存，返回一个Observable
     *
     * @param type 保存的类型
     * @param key  缓存key
     */
    public <T> Observable<T> load(final String key, final Type type) {
        return load(key, type, -1);
    }

    /**
     * 通过Rx的方式获取缓存，返回一个Observable
     *
     * @param type 保存的类型
     * @param key  缓存key
     * @param time 保存时间 ms
     */
    public <T> Observable<T> load(final String key, final Type type, final long time) {
        return Observable.create(new SimpleSubscribe<RealEntity<T>>() {
            @Override
            RealEntity<T> execute() {
                return cacheCore.load(type, key, time);
            }
        }).map(new Function<RealEntity<T>, T>() {
            @Override
            public T apply(RealEntity<T> entity) throws Exception {
                return entity.getData();
            }
        });
    }

    /**
     * 同步保存
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean put(String key, T value) {
        return put(key, value, -1);
    }

    /**
     * 同步保存
     *
     * @param <T>
     * @param key
     * @param value
     * @param cacheTime 毫秒ms
     */
    public <T> boolean put(String key, T value, long cacheTime) {
        if (cacheTime < -1) {
            cacheTime = -1;
        }
        RealEntity<T> entity = new RealEntity<>(value, cacheTime);
        entity.setUpdateDate(System.currentTimeMillis());
        return cacheCore.save(key, entity);
    }

    /**
     * 通过Rx的方式保存，返回一个Observable
     *
     * @param key   缓存key
     * @param value 缓存Value
     */
    public <T> Observable<Boolean> save(final String key, final T value) {
        return save(key, value, -1);
    }

    public <T> Observable<Boolean> save(final String key, final T value, final long cacheTime) {
        return Observable.create(new SimpleSubscribe<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                long time;
                if (cacheTime < -1) {
                    time = -1;
                } else {
                    time = cacheTime;
                }
                RealEntity<T> entity = new RealEntity<>(value, time);
                entity.setUpdateDate(System.currentTimeMillis());
                return cacheCore.save(key, entity);
            }
        });
    }

    public boolean containsKey(String key) {
        return cacheCore.containsKey(key);
    }


    /**
     * 删除缓存
     */
    public Observable<Boolean> remove(final String key) {
        return Observable.create(new SimpleSubscribe<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                return cacheCore.remove(key);
            }
        });
    }

    /**
     * 清空缓存
     */
    public Observable<Boolean> clear() {
        return Observable.create(new SimpleSubscribe<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                return cacheCore.clear();
            }
        });
    }

    /**
     * 利用反射，加载缓存策略模型
     */
    private IStrategy loadStrategy(CacheMode cacheMode) {
        IStrategy strategy = null;
        switch (cacheMode) {
            case DEFAULT:
                strategy = new NoStrategy();
                break;
            case FIRSTREMOTE:
                strategy = new FirstRemoteStrategy();
                break;
            case FIRSTCACHE:
                strategy = new FirstCacheStrategy();
                break;
            case ONLYREMOTE:
                strategy = new OnlyRemoteStrategy();
                break;
            case ONLYCACHE:
                strategy = new OnlyCacheStrategy();
                break;
            case CACHEANDREMOTE:
                strategy = new CacheAndRemoteStrategy();
                break;
            case CACHEANDREMOTEDISTINCT:
                strategy = new CacheAndRemoteDistinctStrategy();
                break;
            default:
                break;
        }
        return strategy;
    }

    public long getCacheTime() {
        return cacheTime;
    }


    public CacheCore getCacheCore() {
        return cacheCore;
    }

    public IDiskConverter getDiskConverter() {
        return diskConverter;
    }

    public File getDiskDir() {
        return diskDir;
    }

    public int getCacheVersion() {
        return cacheVersion;
    }

    public long getDiskMaxSize() {
        return diskMaxSize;
    }

    public static final class Builder {
        private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
        private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
        public static final long CACHE_NEVER_EXPIRE = -1;//永久不过期
        private int cacheVersion;
        private long diskMaxSize;
        private File diskDir;
        private IDiskConverter diskConverter;
        private long cacheTime;

        public Builder() {
            switch (RxConfig.get().getConverterType()) {
                case JACKSON:
                    this.diskConverter = new JacksonDiskConverter();
                    break;
                case FASTJSON:
                    this.diskConverter = new FastJsonDiskConvert();
                    break;
                case GSON:
                default:
                    this.diskConverter = new GsonDiskConverter();
                    break;
            }
            cacheTime = CACHE_NEVER_EXPIRE;
            cacheVersion = 1;
        }

        public Builder(RxCache rxCache) {
            this.cacheVersion = rxCache.cacheVersion;
            this.diskMaxSize = rxCache.diskMaxSize;
            this.diskDir = rxCache.diskDir;
            this.diskConverter = rxCache.diskConverter;
            this.cacheTime = rxCache.cacheTime;
        }

        /**
         * 不设置，默认为1
         */
        public Builder cacheVersion(int appVersion) {
            this.cacheVersion = appVersion;
            return this;
        }

        /**
         * 默认为缓存路径
         *
         * @param directory
         * @return
         */
        public Builder cacheDir(File directory) {
            this.diskDir = directory;
            return this;
        }


        public Builder diskConverter(IDiskConverter converter) {
            this.diskConverter = converter;
            return this;
        }

        /**
         * 不设置， 默为认50MB
         */
        public Builder cacheMaxSize(long maxSize) {
            this.diskMaxSize = maxSize;
            return this;
        }


        public Builder cacheExpTime(long cacheTime) {
            this.cacheTime = cacheTime;
            return this;
        }


        public RxCache build() {
            ObjectHelper.requireNonNull(this.diskDir, "diskDir==null");
            if (!this.diskDir.exists()) {
                this.diskDir.mkdirs();
            }
            if (diskMaxSize <= 0) {
                diskMaxSize = calculateDiskCacheSize(diskDir);
            }
            cacheTime = Math.max(CACHE_NEVER_EXPIRE, this.cacheTime);

            cacheVersion = Math.max(1, this.cacheVersion);

            return new RxCache(this);
        }

        @SuppressWarnings("deprecation")
        private static long calculateDiskCacheSize(File dir) {
            long size = 0;
            try {
                StatFs statFs = new StatFs(dir.getAbsolutePath());
                long available = (( long ) statFs.getBlockCount()) * statFs.getBlockSize();
                size = available / 50;
            } catch (IllegalArgumentException ignored) {
            }
            return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
        }

    }
}
