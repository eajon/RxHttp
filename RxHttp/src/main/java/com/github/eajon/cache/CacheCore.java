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


import android.util.Log;

import com.github.eajon.model.RealEntity;

import java.lang.reflect.Type;

import io.reactivex.internal.functions.ObjectHelper;
import okio.ByteString;


/**
 * @author eajon
 */
public class CacheCore {
    private static final String TAG = "CacheCore";

    private LruDiskCache disk;

    public CacheCore(LruDiskCache disk) {

        this.disk = ObjectHelper.requireNonNull(disk, "disk == null");
    }


    /**
     * 读取
     */
    public synchronized <T> RealEntity<T> load(Type type, String key, long time) {
        String cacheKey = ByteString.of(key.getBytes()).md5().hex();
        Log.d(TAG, "loadCache  key=" + cacheKey);
        if (disk != null) {
            RealEntity<T> result = disk.load(type, cacheKey, time);
            if (result != null) {
                if (result.getCacheTime() == -1 || result.getUpdateDate() + result.getCacheTime() > System.currentTimeMillis()) {
                    // 未过期
                    return result;
                }

                // 已过期
                disk.remove(cacheKey);
            }
        }

        return null;
    }

    /**
     * 保存
     */
    public synchronized <T> boolean save(String key, T value) {
        String cacheKey = ByteString.of(key.getBytes()).md5().hex();
        Log.d(TAG, "saveCache  key=" + cacheKey);
        return disk.save(cacheKey, value);
    }

    /**
     * 是否包含
     *
     * @param key
     * @return
     */
    public synchronized boolean containsKey(String key) {
        String cacheKey = ByteString.of(key.getBytes()).md5().hex();
        Log.d(TAG, "containsCache  key=" + cacheKey);
        if (disk != null) {
            return disk.containsKey(cacheKey);
        }
        return false;
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public synchronized boolean remove(String key) {
        String cacheKey = ByteString.of(key.getBytes()).md5().hex();
        Log.d(TAG, "removeCache  key=" + cacheKey);
        if (disk != null) {
            return disk.remove(cacheKey);
        }
        return true;
    }

    /**
     * 清空缓存
     */
    public synchronized boolean clear() {
        if (disk != null) {
            return disk.clear();
        }
        return false;
    }

}
