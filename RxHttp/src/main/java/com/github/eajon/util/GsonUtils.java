package com.github.eajon.util;

import android.text.TextUtils;

import com.github.eajon.annotation.GsonField;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author WENGYIJIONG
 */

public class GsonUtils {

    private GsonUtils() {
        throw new AssertionError("");
    }

    private final static Gson GSON = new GsonBuilder().
            registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
                @Override
                public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                    if (src == src.longValue()) {
                        return new JsonPrimitive(src.longValue());
                    }
                    return new JsonPrimitive(src);
                }
            }).create();

    public static Gson getGson() {
        return GSON;
    }

    private static Gson buildGson(Object object) {
        return new GsonBuilder()
                .registerTypeAdapter(
                        new TypeToken<Map<String, String>>() {
                        }.getType(),
                        new MapJsonDeserializer(object)).create();
    }

    public static Map<String, Object> object2Map(Object object) {
        Gson gson = buildGson(object);
        return gson.fromJson(gson.toJson(object), new TypeToken<Map<String, String>>() {
        }.getType());
    }

    private final static class MapJsonDeserializer implements JsonDeserializer<Map<String, Object>> {

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
                Field field = ReflectUtils.getDeclaredField(object, entry.getKey());
                if (field != null) {
                    boolean hasName = field.isAnnotationPresent(GsonField.class);
                    if (hasName) {
                        GsonField gsonField = field.getAnnotation(GsonField.class);
                        if (gsonField.require()) {
                            map.put(TextUtils.isEmpty(gsonField.value()) ? entry.getKey() : gsonField.value(), entry.getValue().isJsonPrimitive() ? entry.getValue().getAsString() : entry.getValue().toString());
                        }
                    } else {
                        map.put(entry.getKey(), entry.getValue().isJsonPrimitive() ? entry.getValue().getAsString() : entry.getValue().toString());
                    }
                } else {
                    map.put(entry.getKey(), entry.getValue().isJsonPrimitive() ? entry.getValue().getAsString() : entry.getValue().toString());
                }


            }
            return map;
        }
    }


    public static String parseString(Object response) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(getGson().toJson(response));
        if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
        } else if (jsonElement.isJsonNull()) {
            return JsonNull.INSTANCE.toString();
        } else {
            return jsonElement.toString();
        }
    }

}


