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

package com.github.eajon.util;


import android.text.TextUtils;

import com.github.eajon.cache.RxCache;
import com.github.eajon.cache.RxCacheProvider;
import com.github.eajon.download.DownloadTask;
import com.github.eajon.function.CacheResultFunction;
import com.github.eajon.function.DownloadResponseFunction;
import com.github.eajon.function.ErrorResponseFunction;
import com.github.eajon.function.HttpResponseFunction;
import com.github.eajon.function.RetryExceptionFunction;
import com.github.eajon.upload.MultipartUploadTask;
import com.github.eajon.upload.UploadTask;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * <p>描述：Rx工具类</p>
 * 作者： wengyijiong<br>
 * 日期：  <br>
 * 版本： v1.0<br>
 */
public class RxUtils {


    //线程调度
    public static <T> ObservableTransformer <T, T> io_main() {
        return new ObservableTransformer <T, T>() {
            @Override
            public ObservableSource <T> apply(@NonNull Observable <T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    // 重试
    public static <T> ObservableTransformer <T, T> retryPolicy(final int time) {
        return new ObservableTransformer <T, T>() {
            @Override
            public ObservableSource <T> apply(@NonNull Observable <T> upstream) {
                return upstream
                        .onErrorResumeNext(new ErrorResponseFunction())
                        .retryWhen(new RetryExceptionFunction(time));
            }
        };
    }

    //返回数据转换
    public static <T> ObservableTransformer <T, T> map(final DownloadTask downloadTask, final Type type) {
        return new ObservableTransformer <T, T>() {
            @Override
            public ObservableSource <T> apply(@NonNull Observable <T> upstream) {
                if (downloadTask != null) {
                    return upstream.map(new DownloadResponseFunction(downloadTask));
                } else {
                    return upstream.map(new HttpResponseFunction(type));
                }
            }
        };
    }

    //返回数据转换
    public static <T> ObservableTransformer <T, T> lifeCycle(final LifecycleProvider lifecycle, final ActivityEvent activityEvent, final FragmentEvent fragmentEvent) {
        return new ObservableTransformer <T, T>() {
            @Override
            public ObservableSource <T> apply(@NonNull Observable <T> upstream) {
                if (lifecycle != null) {
                    if (activityEvent != null || fragmentEvent != null) {
                        //两个同时存在,以 activity 为准
                        if (activityEvent != null && fragmentEvent != null) {
                            return upstream.compose(lifecycle.bindUntilEvent(activityEvent));
                        }
                        if (activityEvent != null) {
                            return upstream.compose(lifecycle.bindUntilEvent(activityEvent));
                        }
                        if (fragmentEvent != null) {
                            return upstream.compose(lifecycle.bindUntilEvent(fragmentEvent));
                        }
                    } else {
                        return upstream.compose(lifecycle.bindToLifecycle());
                    }
                }
                return upstream;
            }
        };
    }


    //缓存
    public static <T> ObservableTransformer <T, T> cache(final String cacheKey, final Type type, final boolean isRequest) {
        return new ObservableTransformer <T, T>() {
            @Override
            public ObservableSource <T> apply(@NonNull Observable <T> upstream) {
                if (!isRequest || !RxCacheProvider.isInit() || TextUtils.isEmpty(cacheKey)) {
                    return upstream;
                } else {
                    return upstream
                            .compose(RxCache.generateRxCache(cacheKey).transformer(RxCacheProvider.getCacheMode(), type == null ? String.class : type))
                            .map(new CacheResultFunction());
                }
            }
        };
    }


    //RXbus发射状态
    public static <T> ObservableTransformer <T, T> sendEvent(final DownloadTask downloadTask, final UploadTask uploadTask, final MultipartUploadTask multipartUploadTask, final String eventId, final boolean isStick) {
        return new ObservableTransformer <T, T>() {
            @Override
            public ObservableSource <T> apply(@NonNull Observable <T> upstream) {
                return upstream.doOnSubscribe(new Consumer <Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
//                        LogUtils.e("dialog", "doOnSubscribe");
                        if (downloadTask != null) {
                            downloadTask.setState(DownloadTask.State.LOADING);
                            downloadTask.sendBus(eventId, isStick);
                        } else if (uploadTask != null) {
                            uploadTask.setState(UploadTask.State.LOADING);
                            uploadTask.sendBus(eventId, isStick);
                        } else if (multipartUploadTask != null) {
                            multipartUploadTask.setState(UploadTask.State.LOADING);
                            multipartUploadTask.sendBus(eventId, isStick);
                        }
                    }
                }).doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
//                        LogUtils.e("dialog", "doOnDispose");
                    }
                }).doOnError(new Consumer <Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        LogUtils.e("dialog", "doOnError");
                        if (downloadTask != null) {
                            downloadTask.setState(DownloadTask.State.ERROR);
                            downloadTask.sendBus(eventId, isStick);
                        } else if (uploadTask != null) {
                            uploadTask.setState(UploadTask.State.ERROR);
                            uploadTask.sendBus(eventId, isStick);
                        } else if (multipartUploadTask != null) {
                            multipartUploadTask.setState(UploadTask.State.ERROR);
                            for (UploadTask uploadTask : multipartUploadTask.getUploadTasks()) {
                                uploadTask.setState(UploadTask.State.ERROR);
                            }
                            multipartUploadTask.sendBus(eventId, isStick);
                        }
                    }
                }).doOnNext(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
//                        LogUtils.e("dialog", "doOnNext");
                        if (downloadTask != null) {
                            downloadTask.setState(DownloadTask.State.FINISH);
                            downloadTask.sendBus(eventId, isStick);
                        } else if (uploadTask != null) {
                            uploadTask.setState(UploadTask.State.FINISH);
                            uploadTask.sendBus(eventId, isStick);
                        } else if (multipartUploadTask != null) {
                            multipartUploadTask.setState(UploadTask.State.FINISH);
                            multipartUploadTask.sendBus(eventId, isStick);
                        }
                    }
                }).doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
//                        LogUtils.e("dialog", "doFinally");
                        if (downloadTask != null && !downloadTask.isFinish() && !downloadTask.isError()) {
                            downloadTask.setState(DownloadTask.State.PAUSE);
                            downloadTask.sendBus(eventId, isStick);
                        } else if (uploadTask != null && !uploadTask.isFinish() && !uploadTask.isError()) {
                            uploadTask.setState(UploadTask.State.CANCEL);
                            uploadTask.sendBus(eventId, isStick);
                        } else if (multipartUploadTask != null && !multipartUploadTask.isFinish() && !multipartUploadTask.isError()) {
                            multipartUploadTask.setState(UploadTask.State.CANCEL);
                            for (UploadTask uploadTask : multipartUploadTask.getUploadTasks()) {
                                uploadTask.setState(UploadTask.State.CANCEL);
                            }
                            multipartUploadTask.sendBus(eventId, isStick);
                        }
                    }
                });
            }
        };
    }
}
