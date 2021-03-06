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

package com.github.eajon.converter;


import com.github.eajon.model.RealEntity;
import com.github.eajon.util.GsonUtils;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ConcurrentModificationException;

import okhttp3.internal.Util;

/**
 * @author eajon
 */
@SuppressWarnings(value = "unchecked")
public class GsonDiskConverter implements IDiskConverter {

    public GsonDiskConverter() {

    }

    @Override
    public <T> RealEntity<T> load(InputStream source, Type type) {
        RealEntity<T> entity = null;
        try {
            TypeAdapter<RealEntity<T>> adapter = ( TypeAdapter<RealEntity<T>> ) GsonUtils.getGson().getAdapter(TypeToken.getParameterized(RealEntity.class, type));
            JsonReader jsonReader = GsonUtils.getGson().newJsonReader(new InputStreamReader(source));
            entity = adapter.read(jsonReader);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(source);
        }

        return entity;
    }

    @Override
    public boolean writer(OutputStream sink, Object data) {
        try {
            String json = GsonUtils.getGson().toJson(data);
            byte[] bytes = json.getBytes();
            sink.write(bytes, 0, bytes.length);
            sink.flush();
            return true;
        } catch (JsonIOException | JsonSyntaxException | ConcurrentModificationException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(sink);
        }
        return false;
    }

}
