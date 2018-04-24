package com.creditease.ns.log.util;

import java.io.File;

class Env {
    Env() {
    }

    public static String[] getClassPath() {
        String classPath = System.getProperty("java.class.path");
        String[] dirs;
        if (classPath != null && !"".equals(classPath)) {
            dirs = classPath.split(File.pathSeparator);
        } else {
            dirs = new String[0];
        }

        return dirs;
    }
}
