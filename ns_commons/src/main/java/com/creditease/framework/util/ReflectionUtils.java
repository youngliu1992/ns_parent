//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.creditease.framework.util;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public ReflectionUtils() {
    }

    public static Object getFieldValue(String fieldName, Object target) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(target);
    }

    public static void setFieldValue(String fieldName, Object target, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}
