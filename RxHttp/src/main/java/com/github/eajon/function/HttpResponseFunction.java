package com.github.eajon.function;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.eajon.RxConfig;
import com.github.eajon.util.GsonUtils;
import com.github.eajon.util.JacksonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.lang.reflect.Type;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 服务器结果处理函数
 *
 * @author wengyijiong
 */
public class HttpResponseFunction<T> implements Function<Object, Object> {


    private Type type;

    public HttpResponseFunction(Type type) {
        this.type = type;
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public T apply(@NonNull Object response) throws IOException {
        if (type.equals(String.class)) {
            switch (RxConfig.get().getConverterType()) {
                case JACKSON:
                    JsonNode jsonNode = JacksonUtils.getMapper().readTree(JacksonUtils.getMapper().writeValueAsString(response));
                    if (jsonNode.isTextual()) {
                        return ( T ) jsonNode.asText();
                    } else {
                        return ( T ) jsonNode.toString();
                    }
                case FASTJSON:
                    return ( T ) JSONObject.toJSONString(response);
                case GSON:
                default:
                    return ( T ) GsonUtils.parseString(response);
            }
        } else {
            switch (RxConfig.get().getConverterType()) {
                case JACKSON:
                    JavaType javaType = JacksonUtils.getTypeFactory().constructType(type);
                    return JacksonUtils.getMapper().convertValue(response, javaType);
                case FASTJSON:
                    return JSONObject.parseObject(JSONObject.toJSONString(response), type);
                case GSON:
                default:
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(GsonUtils.getGson().toJson(response));
                    return GsonUtils.getGson().fromJson(jsonElement, type);
            }
        }
    }


}