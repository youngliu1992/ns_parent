//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.creditease.framework.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesUtil {
    private Properties pro = null;

    public PropertiesUtil(String path) throws Exception {
        this.pro = this.loadProperty(path);
    }

    public PropertiesUtil(InputStream inputStream) {
        this.pro = new Properties();

        try {
            this.pro.load(inputStream);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public String getString(String key) {
        try {
            return this.pro.getProperty(key);
        } catch (Exception var3) {
            throw new RuntimeException("key:" + key);
        }
    }

    public String getString(String key, String defaultValue) throws Exception {
        try {
            return this.pro.getProperty(key);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public int getInt(String key) {
        try {
            return Integer.parseInt(this.pro.getProperty(key));
        } catch (Exception var3) {
            throw new RuntimeException("key:" + key);
        }
    }

    public int getInt(String key, int defualtValue) {
        try {
            return Integer.parseInt(this.pro.getProperty(key));
        } catch (Exception var4) {
            return defualtValue;
        }
    }

    public double getDouble(String key) {
        try {
            return Double.parseDouble(this.pro.getProperty(key));
        } catch (Exception var3) {
            throw new RuntimeException("key:" + key);
        }
    }

    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(this.pro.getProperty(key));
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public long getLong(String key) {
        try {
            return Long.parseLong(this.pro.getProperty(key));
        } catch (Exception var3) {
            throw new RuntimeException("key:" + key);
        }
    }

    public long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(this.pro.getProperty(key));
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public float getFloat(String key) {
        try {
            return Float.parseFloat(this.pro.getProperty(key));
        } catch (Exception var3) {
            throw new RuntimeException("key:" + key);
        }
    }

    public float getFloat(String key, float defaultValue) {
        try {
            return Float.parseFloat(this.pro.getProperty(key));
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key) {
        try {
            return Boolean.parseBoolean(this.pro.getProperty(key));
        } catch (Exception var3) {
            throw new RuntimeException("key:" + key);
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(this.pro.getProperty(key));
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public Set<Object> getAllKey() {
        return this.pro.keySet();
    }

    public Collection<Object> getAllValue() {
        return this.pro.values();
    }

    public Map<String, Object> getAllKeyValue() {
        Map<String, Object> mapAll = new HashMap();
        Set<Object> keys = this.getAllKey();
        Iterator it = keys.iterator();

        while(it.hasNext()) {
            String key = it.next().toString();
            mapAll.put(key, this.pro.get(key));
        }

        return mapAll;
    }

    private Properties loadProperty(String name) throws Exception {
        String filePath = null;

        try {
            filePath = Thread.currentThread().getContextClassLoader().getResource(name).getFile();
        } catch (Exception var11) {
            throw new IllegalArgumentException("找不到配置文件:" + name, var11);
        }

        FileInputStream fin = null;
        Properties pro = new Properties();

        try {
            fin = new FileInputStream(filePath);
            pro.load(fin);
        } catch (IOException var10) {
            throw new IllegalArgumentException("找不到配置文件:" + name, var10);
        } finally {
            if (fin != null) {
                fin.close();
            }

        }

        return pro;
    }
}
