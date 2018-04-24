package com.creditease.ns.log;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.util.Loader;
import com.creditease.ns.log.appender.NSLogRollingFileAppender;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class NsLog extends NsLogFace {
    private static final Map<String, NsLog> _pool;

    public NsLog() {
    }

    public static synchronized Set<String> getLoggers() {
        return _pool.keySet();
    }

    public static synchronized void clearLoggers() {
        _pool.clear();
    }

    public static synchronized NsLog getLog(String logKey) {
        NsLog log = (NsLog)_pool.get(logKey);
        if (log == null) {
            log = new NsLog();
            log.setName(logKey);
            _pool.put(logKey, log);
        }

        return log;
    }

    public static NsLog getLog(String category, String moduleName, String mouleDesc) {
        if (null != moduleName && !"".equals(moduleName.trim()) && null != mouleDesc && !"".equals(mouleDesc.trim())) {
            String logKey = category + ".>" + moduleName + "|" + mouleDesc;
            return getLog(logKey);
        } else {
            return getLog(category);
        }
    }

    public static NsLog getFramLog() {
        return getLog("ns.fram");
    }

    public static NsLog getFlowLog() {
        return getLog("ns.flow");
    }

    public static NsLog getMqLog() {
        return getLog("ns.mq");
    }

    public static NsLog getTaskLog() {
        return getLog("ns.task");
    }

    public static NsLog getBizLog() {
        return getLog("ns.biz");
    }

    public static NsLog getFramLog(String moduleName, String moduleDesc) {
        return getLog("ns.fram", moduleName, moduleDesc);
    }

    public static NsLog getFlowLog(String moduleName, String moduleDesc) {
        return getLog("ns.flow", moduleName, moduleDesc);
    }

    public static NsLog getMqLog(String moduleName, String moduleDesc) {
        return getLog("ns.mq", moduleName, moduleDesc);
    }

    public static NsLog getTaskLog(String moduleName, String moduleDesc) {
        return getLog("ns.task", moduleName, moduleDesc);
    }

    public static NsLog getBizLog(String moduleName, String moduleDesc) {
        return getLog("ns.biz", moduleName, moduleDesc);
    }

    static void log(String category, String message, Object... args) {
        getLog(category).info(message, args);
    }

    public static void setSubPrimary(String subPrimaryKey) {
        MDC.put("subprimaryKey", subPrimaryKey);
    }

    public static void setMsgId(String msgId) {
        MDC.put("uuid", msgId);
    }

    public static void removeMsgId() {
        MDC.remove("uuid");
    }

    private static void renameLoggingFile() {
        LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
        context.stop();
        context.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);

        try {
            URL url = getResource("logback.xml", Thread.currentThread().getContextClassLoader());
            if (url == null) {
                throw new RuntimeException("init Log error,can't find logback.xml");
            }

            configurator.doConfigure(url.getFile());
        } catch (JoranException var13) {
            try {
                configurator.doConfigure(getResourceAsStream("logback.xml", Thread.currentThread().getContextClassLoader()));
            } catch (JoranException var12) {
                throw new RuntimeException(var13.getMessage(), var12);
            }
        }

        String configFile = System.getProperty("configfile");
        if (configFile != null && !"".equals(configFile)) {
            configFile = configFile.indexOf(".") != -1 ? configFile.substring(0, configFile.lastIndexOf(".")) : configFile;
            configFile = configFile + "_";
        } else {
            configFile = "";
        }

        Iterator i$ = context.getLoggerList().iterator();

        while(i$.hasNext()) {
            Logger logger = (Logger)i$.next();
            Iterator index = logger.iteratorForAppenders();

            while(index.hasNext()) {
                Appender<ILoggingEvent> appender = (Appender)index.next();
                if (appender instanceof NSLogRollingFileAppender) {
                    NSLogRollingFileAppender fileAppender = (NSLogRollingFileAppender)appender;
                    fileAppender.stop();
                    String fileName = fileAppender.getFile().replace("configfile_", configFile);
                    fileAppender.setFile(fileName);
                    TriggeringPolicy triggeringPolicy = fileAppender.getTriggeringPolicy();
                    if (triggeringPolicy instanceof TimeBasedRollingPolicy) {
                        TimeBasedRollingPolicy timeBasedRollingPolicy = (TimeBasedRollingPolicy)triggeringPolicy;
                        String fileNamePattern = timeBasedRollingPolicy.getFileNamePattern().replace("configfile_", configFile);
                        timeBasedRollingPolicy.setFileNamePattern(fileNamePattern);
                        timeBasedRollingPolicy.stop();
                        timeBasedRollingPolicy.start();
                    }

                    fileAppender.realStart();
                }
            }
        }

        context.start();
    }

    private static URL getResource(String filename, ClassLoader myClassLoader) {
        URL url = Loader.getResource(filename, myClassLoader);
        return url;
    }

    private static InputStream getResourceAsStream(String filename, ClassLoader myClassLoader) {
        return myClassLoader.getResourceAsStream(filename);
    }

    static {
        renameLoggingFile();
        _pool = new HashMap();
    }
}
