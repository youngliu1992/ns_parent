package com.creditease.ns.log;

import java.util.UUID;

public class LogUtils {
    private static final char[] DIGITS64 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_".toCharArray();

    public LogUtils() {
    }

    public static String getSimpleMethodName() {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste.length <= 2 ? "" : ste[2].getMethodName();
    }

    public static String getFullyMethodName() {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        return ste.length <= 2 ? "" : ste[2].getClassName() + "#" + ste[2].getMethodName();
    }

    private static String toIDString(long l) {
        char[] buf = "00000000000".toCharArray();
        int length = 11;
        long least = 63L;

        do {
            --length;
            buf[length] = DIGITS64[(int)(l & least)];
            l >>>= 6;
        } while(l != 0L);

        return new String(buf);
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String getShortUUID() {
        UUID u = UUID.randomUUID();
        return toIDString(u.getMostSignificantBits()) + toIDString(u.getLeastSignificantBits());
    }
}
