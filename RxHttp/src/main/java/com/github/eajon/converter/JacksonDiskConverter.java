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


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.github.eajon.model.RealEntity;
import com.github.eajon.util.JacksonUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import okhttp3.internal.Util;

/**
 * @author eajon
 */
public class JacksonDiskConverter implements IDiskConverter {

    public JacksonDiskConverter() {
    }

    @Override
    public <T> RealEntity<T> load(InputStream source, Type type) {
        RealEntity<T> entity = null;
        try {
            JavaType javaType = JacksonUtils.getTypeFactory().constructParametricType(RealEntity.class, JacksonUtils.getTypeFactory().constructType(type));
            JsonParser parser = new JsonFactory().createParser(source);
            entity = JacksonUtils.getMapper().readValue(parser, javaType);
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
            String json = JacksonUtils.getMapper().writeValueAsString(data);
            byte[] bytes = json.getBytes();
            sink.write(bytes, 0, bytes.length);
            sink.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(sink);
        }
        return false;
    }

}
