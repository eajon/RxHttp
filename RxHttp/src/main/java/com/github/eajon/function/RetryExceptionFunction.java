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

package com.github.eajon.function;


import com.github.eajon.util.LoggerUtils;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

/**
 *
 * @author eajon
 */
public class RetryExceptionFunction implements Function<Observable<? extends Throwable>, Observable> {
    /** retry次数*/
    private int count;
    /** 延迟*/
    private long delay = 500;


    public RetryExceptionFunction(int count) {
        this.count = count;
    }


    @Override
    public Observable apply(@NonNull Observable<? extends Throwable> observable) {
        return observable.zipWith(Observable.range(1, count + 1), new BiFunction<Throwable, Integer, Wrapper>() {
            @Override
            public Wrapper apply(@NonNull Throwable throwable, @NonNull Integer integer) throws Exception {
                return new Wrapper(throwable, integer);
            }
        }).flatMap(new Function<Wrapper, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(@NonNull Wrapper wrapper) throws Exception {
                if (wrapper.index > 1) {
                    LoggerUtils.info("retryTime：" + (wrapper.index));
                }
                if ((wrapper.throwable instanceof SocketException
                        || wrapper.throwable instanceof SocketTimeoutException
                        || wrapper.throwable instanceof TimeoutException)
                        && wrapper.index < count + 1) {
                    return Observable.timer(delay, TimeUnit.MILLISECONDS);
                }
                return Observable.error(wrapper.throwable);
            }
        });
    }

    private class Wrapper {
        private int index;
        private Throwable throwable;

        public Wrapper(Throwable throwable, int index) {
            this.index = index;
            this.throwable = throwable;
        }
    }

}
