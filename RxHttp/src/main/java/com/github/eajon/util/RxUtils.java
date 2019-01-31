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

import com.github.eajon.RxConfig;
import com.github.eajon.cache.RxCache;
import com.github.eajon.enums.CacheMode;
import com.github.eajon.enums.RequestType;
import com.github.eajon.function.CacheResultFunction;
import com.github.eajon.function.DownloadResponseFunction;
import com.github.eajon.function.ErrorResponseFunction;
import com.github.eajon.function.HttpResponseFunction;
import com.github.eajon.function.RetryExceptionFunction;
import com.github.eajon.observer.DownloadObserver;
import com.github.eajon.observer.UploadObserver;
import com.github.eajon.task.BaseTask;
import com.github.eajon.task.DownloadTask;
import com.github.eajon.task.MultiUploadTask;
import com.github.eajon.task.UploadTask;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
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

    //返回数据转换
    @SuppressWarnings("unchecked")
    public static <T> ObservableTransformer<T, T> map(final RequestType requestType, final Type type) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                if (requestType == RequestType.DOWNLOAD) {
                    return upstream.map(new DownloadResponseFunction());
                }
                return upstream.map(new HttpResponseFunction(type));
            }
        };
    }


    //缓存
    @SuppressWarnings("unchecked")
    public static <T> ObservableTransformer<T, T> cache(final RequestType requestType, final Type type, final String cacheKey) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                RxCache rxCache = RxConfig.get().getRxCache();
                CacheMode cacheMode = RxConfig.get().getCacheMode();
                if (requestType != RequestType.REQUEST || rxCache == null || TextUtils.isEmpty(cacheKey)) {
                    return upstream;
                }
                return upstream
                        .compose(rxCache.transformer(cacheMode, type == null ? String.class : type, cacheKey))
                        .map(new CacheResultFunction());

            }
        };
    }

    // 重试
    @SuppressWarnings("unchecked")
    public static <T> ObservableTransformer<T, T> retryPolicy(final int time) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .onErrorResumeNext(new ErrorResponseFunction())
                        .retryWhen(new RetryExceptionFunction(time));
            }
        };
    }

    //生命周期关联
    @SuppressWarnings("unchecked")
    public static <T> ObservableTransformer<T, T> lifeCycle(final LifecycleProvider lifecycle, final ActivityEvent activityEvent, final FragmentEvent fragmentEvent) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                if (lifecycle != null) {
                    if (activityEvent != null || fragmentEvent != null) {
                        //两个同时存在,以 activity 为准
                        if (activityEvent != null) {
                            return upstream.compose(lifecycle.bindUntilEvent(activityEvent));
                        } else {
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


    //RXbus发射状态
    @SuppressWarnings("unchecked")
    public static <T> ObservableTransformer<T, T> sendEvent(final BaseTask task, final Observer observer) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                if (task == null) {
                    return upstream;
                }
                return upstream.doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        task.setState(BaseTask.State.LOADING);
                        if (task instanceof MultiUploadTask) {
                            for (UploadTask uploadTask : (( MultiUploadTask ) task).getUploadTasks()) {
                                uploadTask.setState(UploadTask.State.WAITING);
                            }
                        }
                        if (observer instanceof UploadObserver) {
                            (( UploadObserver ) observer).onProgress(task);
                        }
                        if (observer instanceof DownloadObserver) {
                            (( DownloadObserver ) observer).onProgress(( DownloadTask ) task);
                        }

                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        task.setState(UploadTask.State.ERROR);
                        if (task instanceof MultiUploadTask) {
                            for (UploadTask uploadTask : (( MultiUploadTask ) task).getUploadTasks()) {
                                uploadTask.setState(UploadTask.State.ERROR);
                            }
                        }
                        if (observer instanceof UploadObserver) {
                            (( UploadObserver ) observer).onProgress(task);
                        }
                        if (observer instanceof DownloadObserver) {
                            (( DownloadObserver ) observer).onProgress(( DownloadTask ) task);
                        }

                    }
                }).doOnNext(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        task.setState(BaseTask.State.FINISH);
                        if (observer instanceof UploadObserver) {
                            (( UploadObserver ) observer).onProgress(task);
                        }
                        if (observer instanceof DownloadObserver) {
                            (( DownloadObserver ) observer).onProgress(( DownloadTask ) task);
                        }

                    }
                }).doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (!task.isFinish() && !task.isError()) {
                            if (task instanceof DownloadTask) {
                                task.setState(BaseTask.State.PAUSE);
                                if (observer instanceof DownloadObserver) {
                                    (( DownloadObserver ) observer).onPause(( DownloadTask ) task);
                                }
                            } else if (task instanceof UploadTask) {
                                task.setState(BaseTask.State.CANCEL);
                                if (observer instanceof UploadObserver) {
                                    (( UploadObserver ) observer).onCancel();
                                }
                            } else if (task instanceof MultiUploadTask) {
                                task.setState(BaseTask.State.CANCEL);
                                for (UploadTask uploadTask : (( MultiUploadTask ) task).getUploadTasks()) {
                                    uploadTask.setState(UploadTask.State.CANCEL);
                                }
                                if (observer instanceof UploadObserver) {
                                    (( UploadObserver ) observer).onCancel();
                                }
                            }
                        }
                    }
                });
            }
        };
    }

    //线程调度
    public static <T> ObservableTransformer<T, T> io_main() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
