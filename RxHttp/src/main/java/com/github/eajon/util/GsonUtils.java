package com.github.eajon.util;

import android.text.TextUtils;

import com.github.eajon.annotation.Name;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GsonUtils {

    public static Gson buildGson(Object object) {
        return new GsonBuilder()
                .registerTypeAdapter(
                        new TypeToken<Map<String, String>>() {
                        }.getType(),
                        new MapJsonDeserializer(object)).create();
    }

    public static Map<String, Object> objectToMap(Object object) {
        Gson gson = buildGson(object);
        return gson.fromJson(gson.toJson(object), new TypeToken<Map<String, String>>() {
        }.getType());
    }

    private static class MapJsonDeserializer implements JsonDeserializer<Map<String, Object>> {

        private Object object;

        private MapJsonDeserializer(Object object) {
            this.object = object;
        }

        @Override
        public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Map<String, Object> map = new HashMap<>();
            JsonObject jsonObject = json.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                Field field = getDeclaredField(object, entry.getKey());
                boolean hasName = field.isAnnotationPresent(Name.class);
                if (hasName) {
                    Name name = field.getAnnotation(Name.class);
                    if (name.require()) {
                        map.put(TextUtils.isEmpty(name.value()) ? entry.getKey() : name.value(), entry.getValue().isJsonPrimitive() ? entry.getValue().getAsString() : entry.getValue().toString());
                    }
                } else {
                    map.put(entry.getKey(), entry.getValue().isJsonPrimitive() ? entry.getValue().getAsString() : entry.getValue().toString());
                }

            }
            return map;
        }
    }

    public static Field getDeclaredField(Object object, String fieldName) {
        Field field = null;

        Class<?> clazz = object.getClass();

        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (NoSuchFieldException e) {
                //这里甚么都不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会进入
            }
        }

        return field;
    }

}


