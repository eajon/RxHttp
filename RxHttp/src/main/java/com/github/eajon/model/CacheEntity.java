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

package com.github.eajon.model;


import java.io.Serializable;

/**
 *
 * @author eajon
 */
public class CacheEntity<T> implements Serializable {
    private boolean isFromCache;
    private T data;

    public CacheEntity() {
    }

    public CacheEntity(boolean isFromCache) {
        this.isFromCache = isFromCache;
    }

    public CacheEntity(boolean isFromCache, T data) {
        this.isFromCache = isFromCache;
        this.data = data;
    }

    public boolean isFromCache() {
        return isFromCache;
    }

    public T getData() {
        return data;
    }


    @Override
    public String toString() {
        return "CacheEntity{" +
                "isCache=" + isFromCache +
                ", data=" + data +
                '}';
    }
}
