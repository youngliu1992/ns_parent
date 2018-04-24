package com.creditease.ns.mq.utils;

public class BytesHelper {
    public BytesHelper() {
    }

    public static String bytesToStr(byte[] target) {
        StringBuffer buf = new StringBuffer();
        int i = 0;

        for(int j = target.length; i < j; ++i) {
            buf.append(target[i]);
            buf.append(",");
        }

        return buf.toString();
    }

    public static byte[] strToBytes(String str) {
        byte[] buf = new byte[str.length()];

        for(int i = 0; i < str.length(); ++i) {
            buf[i] = (byte)str.charAt(i);
        }

        return buf;
    }
}
