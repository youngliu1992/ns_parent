package com.creditease.framework.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;

public class ProtoStuffSerializeUtil {
    public ProtoStuffSerializeUtil() {
    }

    /** @deprecated */
    @Deprecated
    public static <T> byte[] serialize(T t, Class<T> cl) {
        Schema<T> schema = RuntimeSchema.getSchema(cl);
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(1024);
        return ProtostuffIOUtil.toByteArray(t, schema, linkedBuffer);
    }

    /** @deprecated */
    @Deprecated
    public static <T> byte[] serialize(List<T> lst, Class<T> cl) throws IOException {
        if (lst == null) {
            return new byte[0];
        } else {
            Schema<T> schema = RuntimeSchema.getSchema(cl);
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            LinkedBuffer linkedBuffer = LinkedBuffer.allocate(1024);
            ProtostuffIOUtil.writeListTo(arrayOutputStream, lst, schema, linkedBuffer);
            byte[] bs = arrayOutputStream.toByteArray();
            arrayOutputStream.close();
            return bs;
        }
    }

    public static byte[] serializeForCommon(Object o) throws IOException {
        ProtoStuffSerializeUtil.ObjectWrapper objectWrapper = new ProtoStuffSerializeUtil.ObjectWrapper();
        objectWrapper.setWrappedStuff(o);
        Schema<ProtoStuffSerializeUtil.ObjectWrapper> schema = RuntimeSchema.getSchema(ProtoStuffSerializeUtil.ObjectWrapper.class);
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(1024);
        return ProtostuffIOUtil.toByteArray(objectWrapper, schema, linkedBuffer);
    }

    /** @deprecated */
    @Deprecated
    public static <T> List<T> unSerializeForList(byte[] bs, Class<T> cl) throws IOException {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bs);
        Schema<T> schema = RuntimeSchema.getSchema(cl);
        List<T> lst = ProtostuffIOUtil.parseListFrom(arrayInputStream, schema);
        arrayInputStream.close();
        return lst;
    }

    /** @deprecated */
    @Deprecated
    public static <T> T unSerialize(byte[] bs, Class<T> cl) throws InstantiationException, IllegalAccessException {
        Constructor<T>[] constructors = (Constructor[])cl.getConstructors();
        T t = cl.newInstance();
        return unSerialize(bs, cl, t);
    }

    /** @deprecated */
    @Deprecated
    public static <T> T unSerialize(byte[] bs, Class<T> cl, T t) throws InstantiationException, IllegalAccessException {
        Schema<T> schema = RuntimeSchema.getSchema(cl);
        ProtostuffIOUtil.mergeFrom(bs, t, schema);
        return t;
    }

    public static Object unSerializeForCommon(byte[] bs) throws InstantiationException, IllegalAccessException {
        Schema<ProtoStuffSerializeUtil.ObjectWrapper> schema = RuntimeSchema.getSchema(ProtoStuffSerializeUtil.ObjectWrapper.class);
        ProtoStuffSerializeUtil.ObjectWrapper objectWrapper = new ProtoStuffSerializeUtil.ObjectWrapper();
        ProtostuffIOUtil.mergeFrom(bs, objectWrapper, schema);
        return objectWrapper.getWrappedStuff();
    }

    static class ObjectWrapper {
        private Object wrappedStuff;

        ObjectWrapper() {
        }

        public Object getWrappedStuff() {
            return this.wrappedStuff;
        }

        public void setWrappedStuff(Object wrappedStuff) {
            this.wrappedStuff = wrappedStuff;
        }
    }
}
