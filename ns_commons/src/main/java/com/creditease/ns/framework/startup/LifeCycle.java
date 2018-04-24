package com.creditease.ns.framework.startup;

public interface LifeCycle {
    void startUp() throws Exception;

    void destroy() throws Exception;
}