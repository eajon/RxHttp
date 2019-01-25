package com.github.eajon.function;

import com.github.eajon.util.LoggerUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 服务器结果处理函数
 *
 * @author wengyijiong
 */
public class HttpResponseFunction<T> implements Function<JsonElement, Object> {


    private Type type;

    public HttpResponseFunction(Type type) {
        this.type = type;
    }

    @Override
    public Object apply(@NonNull JsonElement response) {
        //打印服务器回传结果
        LoggerUtils.json(response.toString());
        /*此处不再处理业务相关逻辑交由开发者重写httpCallback*/
        if (type == null) {
            return toStringResponse(response);
        } else {
            if (type instanceof Class) {
                Class clazz = ( Class ) type;
                if (clazz.getName().equals(String.class.getName())) {
                    return toStringResponse(response);
                } else {
                    if (clazz.isPrimitive()) {
                        throw new JsonParseException("Not Support Primitive Type,Please Use String Instead!");
                    }
                }
            }
        }
        return new Gson().fromJson(response, type);
    }


    private String toStringResponse(JsonElement response) {
        if (response.isJsonPrimitive()) {
            return response.getAsString();
        } else if (response.isJsonNull()) {
            return JsonNull.INSTANCE.toString();
        } else {
            return response.toString();
        }
    }
}