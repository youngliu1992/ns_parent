package com.creditease.ns.log.converter;

import ch.qos.logback.classic.pattern.ClassOfCallerConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class ClassNameConverter extends ClassOfCallerConverter {
    public ClassNameConverter() {
    }

    protected String getFullyQualifiedName(ILoggingEvent event) {
        StackTraceElement[] cda = event.getCallerData();
        if (cda != null) {
            if (cda.length > 1) {
                return cda[1].getClassName();
            }

            if (cda.length == 1) {
                return cda[0].getClassName();
            }
        }

        return "?";
    }
}
