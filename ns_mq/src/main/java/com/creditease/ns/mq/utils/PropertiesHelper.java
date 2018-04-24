package com.creditease.ns.mq.utils;

import com.creditease.ns.log.NsLog;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class PropertiesHelper {
    private static final NsLog initLog = NsLog.getMqLog("NS_MQ", "初始化");

    public PropertiesHelper() {
    }

    public static Properties getMQProperteis(String filename) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
        initLog.info("读取配置文件路径:{}", new Object[]{url});
        if (url == null) {
            return null;
        } else {
            Properties properties = new Properties();

            try {
                properties.load(url.openStream());
                return properties;
            } catch (IOException var4) {
                return null;
            }
        }
    }
}