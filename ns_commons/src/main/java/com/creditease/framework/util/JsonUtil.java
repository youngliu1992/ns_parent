//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.creditease.framework.util;

import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.ExchangeKey;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.lang.reflect.Type;

public class JsonUtil {
    public static final ObjectMapper mapper = new ObjectMapper();

    public JsonUtil() {
    }

    public static String jsonFromObject(Object object) throws Exception {
        return mapper.writeValueAsString(object);
    }

    public static String jsonFromObject(Object object, Type genericType) throws Exception {
        return mapper.writer().withType(mapper.constructType(genericType)).writeValueAsString(object);
    }

    public static <T> T objectFromJson(String json, Class<T> klass) throws Exception {
        T object = mapper.readValue(json, klass);
        return object;
    }

    public static <T> T objectFromJson(String json, TypeReference<T> klass) throws Exception {
        T object = mapper.readValue(json, klass);
        return object;
    }

    public static <T> T objectFromJson(String json, Type type) throws Exception {
        T object = mapper.readValue(json, mapper.constructType(type));
        return object;
    }

    public static Object convertToSpecialedType(Object o, Class t) throws Exception {
        return objectFromJson(jsonFromObject(o), t);
    }

    public static <T> T convertToTypeReferenceType(Object o, TypeReference<T> t) throws Exception {
        return objectFromJson(jsonFromObject(o), t);
    }

    public static <T> T convertToTypeReferenceType(ServiceMessage serviceMessage, ExchangeKey key, TypeReference<T> t) throws Exception {
        Object o = serviceMessage.getExchangeByType(key, Object.class);
        return convertToTypeReferenceType(o, t);
    }

    public static <T> T convertToTypeReferenceType(Object o, Class<T> clazz) throws Exception {
        return objectFromJson(jsonFromObject(o), new TypeReference<T>() {
        });
    }

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.CREATOR, Visibility.PUBLIC_ONLY);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    }
}
