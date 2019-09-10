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

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    private JacksonUtils() {
        throw new AssertionError("");
    }


    public static ObjectMapper getMapper() {
        return OBJECT_MAPPER;
    }

    public static JsonFactory getFactory() {
        return getMapper().getFactory();
    }

    public static TypeFactory getTypeFactory() {
        return getMapper().getTypeFactory();
    }

}

