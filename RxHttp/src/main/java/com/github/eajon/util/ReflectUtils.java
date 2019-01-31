package com.github.eajon.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectUtils {

    public static Field getDeclaredField(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                assert clazz != null;
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                //这里甚么都不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会进入
            }
        }
        return null;
    }


    //获取泛型类的type
    public static Type getParameterizedType(Object object) {
        Class clazz = object.getClass();
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType p = ( ParameterizedType ) type;
            return p.getActualTypeArguments()[0];
        } else {
            return null;
        }
    }

}
