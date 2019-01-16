package com.github.eajon.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GsonUtils {

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                    new TypeToken<Map<String, Object>>() {
                    }.getType(),
                    new JsonDeserializer<Map<String, Object>>() {
                        @Override
                        public Map<String, Object> deserialize(
                                JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
                            Map<String, Object> map = new HashMap<>();
                            JsonObject jsonObject = json.getAsJsonObject();
                            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                            for (Map.Entry<String, JsonElement> entry : entrySet) {
                                map.put(entry.getKey(), entry.getValue());
                            }
                            return map;
                        }
                    }).create();

    public static Map<String, Object> objectToMap(Object object) {
        Map<String, Object> map = gson.fromJson(gson.toJson(object), new TypeToken<Map<String, Object>>() {
        }.getType());
        return map;
    }
}


