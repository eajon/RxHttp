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
        Map<String, Object> map = gson.fromJson(gson.toJson(object), new TypeToken<Map<String, String>>() {
        }.getType());
        return map;
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
            Class clazz = object.getClass();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                try {
                    Field field = clazz.getDeclaredField(entry.getKey());
                    boolean hasName = field.isAnnotationPresent(Name.class);
                    if (hasName) {
                        Name name = field.getAnnotation(Name.class);
                        if (name.require()) {
                            map.put(TextUtils.isEmpty(name.value()) ? entry.getKey() : name.value(), entry.getValue().isJsonObject() ? entry.getValue().toString() : entry.getValue().getAsString());
                        }
                    } else {
                        map.put(entry.getKey(), entry.getValue().isJsonObject() ? entry.getValue().toString() : entry.getValue().getAsString());
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            return map;
        }
    }
}


