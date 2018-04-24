package com.creditease.ns.log.converter;

import ch.qos.logback.classic.pattern.ClassOfCallerConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class ClassNameAndLineNumberConverter extends ClassOfCallerConverter {
    public ClassNameAndLineNumberConverter() {
    }

    protected String getFullyQualifiedName(ILoggingEvent event) {
        StackTraceElement[] cda = event.getCallerData();
        if (cda != null && cda.length > 0) {
            String loggerName = event.getLoggerName();
            int index = loggerName.indexOf(".>");
            loggerName = index != -1 ? loggerName.substring(0, index) : loggerName;
            return loggerName;
        } else {
            return "?";
        }
    }
}
