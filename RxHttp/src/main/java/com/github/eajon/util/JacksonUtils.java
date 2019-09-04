package com.github.eajon.util;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * @author WENGYIJIONG
 */

public class JacksonUtils {

    private volatile static ObjectMapper mapper;

    private JacksonUtils() {
        throw new AssertionError("");
    }


    public static ObjectMapper getMapper() {
        if (mapper == null) {
            synchronized (JacksonUtils.class) {
                if (mapper == null) {
                    mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                }
            }
        }
        return mapper;
    }

    public static JsonFactory getFactory() {
        return getMapper().getFactory();
    }

    public static TypeFactory getTypeFactory() {
        return getMapper().getTypeFactory();
    }

}

