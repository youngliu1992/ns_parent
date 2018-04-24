package com.creditease.ns.log;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.util.Loader;
import com.creditease.ns.log.appender.NSLogRollingFileAppender;
import java.net.URL;
import java.util.Iterator;
import org.slf4j.LoggerFactory;

public class LogSetting {
    private String logPrefix;
    private String logPath;

    public LogSetting() {
    }

    public LogSetting setAll(String logPrefix, String logPath) {
        this.logPrefix = logPrefix;
        this.logPath = logPath;
        return this;
    }

    public LogSetting setLogPrefix(String logPrefix) {
        this.logPrefix = logPrefix;
        return this;
    }

    public LogSetting setLogPath(String logPath) {
        this.logPath = logPath;
        return this;
    }

    public void init() {
        this.renameLoggingFile(this.logPrefix, this.logPath);
    }

    private void renameLoggingFile(String logPrefix, String logPath) {
        LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory();
        context.stop();
        context.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);

        try {
            URL url = this.getResource("logback.xml", Thread.currentThread().getContextClassLoader());
            if (url == null) {
                throw new RuntimeException("init Log error,can't find logback.xml");
            }

            configurator.doConfigure(url.getFile());
        } catch (JoranException var16) {
            throw new RuntimeException(var16.getMessage(), var16);
        }

        if (logPath == null) {
            logPath = System.getProperty("user.dir") + "/log/";
        }

        System.out.println("【NS_LOG】 logPath:" + logPath);
        if (logPrefix == null) {
            logPrefix = "";
        } else {
            logPrefix = logPrefix + "_";
        }

        System.out.println("【NS_LOG】 logPrefix:" + logPrefix);
        Iterator i$ = context.getLoggerList().iterator();

        label66:
        while(i$.hasNext()) {
            Logger logger = (Logger)i$.next();
            Iterator index = logger.iteratorForAppenders();

            while(true) {
                while(true) {
                    if (!index.hasNext()) {
                        continue label66;
                    }

                    Appender<ILoggingEvent> appender = (Appender)index.next();
                    if (appender instanceof NSLogRollingFileAppender) {
                        NSLogRollingFileAppender fileAppender = (NSLogRollingFileAppender)appender;
                        fileAppender.stop();
                        String fileName = fileAppender.getFile();
                        fileAppender.setFile(logPath + logPrefix + fileName);
                        System.out.println("【NS_LOG】 logFile:" + logPath + logPrefix + fileName);
                        TriggeringPolicy triggeringPolicy = fileAppender.getTriggeringPolicy();
                        if (triggeringPolicy instanceof TimeBasedRollingPolicy) {
                            TimeBasedRollingPolicy timeBasedRollingPolicy = (TimeBasedRollingPolicy)triggeringPolicy;
                            String fileNamePattern = timeBasedRollingPolicy.getFileNamePattern();
                            timeBasedRollingPolicy.setFileNamePattern(logPath + logPrefix + fileNamePattern);
                            System.out.println("【NS_LOG】 logFile:" + logPath + logPrefix + fileNamePattern);
                            timeBasedRollingPolicy.stop();
                            timeBasedRollingPolicy.start();
                        }

                        fileAppender.realStart();
                    } else if (appender.getName().equals("CoalescingStatistics") && appender instanceof AppenderAttachable) {
                        Iterator appenderIterator = ((AppenderAttachable)appender).iteratorForAppenders();

                        while(appenderIterator.hasNext()) {
                            Appender attachAppender = (Appender)appenderIterator.next();
                            if (attachAppender instanceof NSLogRollingFileAppender) {
                                NSLogRollingFileAppender fileAppender = (NSLogRollingFileAppender)attachAppender;
                                fileAppender.stop();
                                String fileName = fileAppender.getFile();
                                fileAppender.setFile(logPath + logPrefix + fileName);
                                System.out.println("【NS_LOG】 logFile:" + logPath + logPrefix + fileName);
                                TriggeringPolicy triggeringPolicy = fileAppender.getTriggeringPolicy();
                                if (triggeringPolicy instanceof TimeBasedRollingPolicy) {
                                    TimeBasedRollingPolicy timeBasedRollingPolicy = (TimeBasedRollingPolicy)triggeringPolicy;
                                    String fileNamePattern = timeBasedRollingPolicy.getFileNamePattern();
                                    timeBasedRollingPolicy.setFileNamePattern(logPath + logPrefix + fileNamePattern);
                                    System.out.println("【NS_LOG】 logFile:" + logPath + logPrefix + fileNamePattern);
                                    timeBasedRollingPolicy.stop();
                                    timeBasedRollingPolicy.start();
                                }

                                fileAppender.realStart();
                            }
                        }
                    }
                }
            }
        }

        context.start();
    }

    private URL getResource(String filename, ClassLoader myClassLoader) {
        URL url = Loader.getResource(filename, myClassLoader);
        return url;
    }
}
