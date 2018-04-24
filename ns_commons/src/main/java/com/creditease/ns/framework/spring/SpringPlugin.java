//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.creditease.ns.framework.spring;

public interface SpringPlugin {
    Object getBean(String var1);

    Object getBeanByClassName(Class<?> var1);

    void init() throws Exception;
}
