package com.creditease.ns.log;

public interface NsLogInf {
    void debug(String var1, Object... var2);

    void info(String var1, Object... var2);

    void warn(String var1, Object... var2);

    void error(Throwable var1, String var2, Object... var3);

    boolean isDebugEnabled();

    boolean isInfoEnabled();
}
