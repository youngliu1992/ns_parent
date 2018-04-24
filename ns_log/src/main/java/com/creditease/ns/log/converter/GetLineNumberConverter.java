package com.creditease.ns.log.converter;

import ch.qos.logback.classic.pattern.LineOfCallerConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class GetLineNumberConverter extends LineOfCallerConverter {
    public GetLineNumberConverter() {
    }

    public String convert(ILoggingEvent le) {
        StackTraceElement[] cda = le.getCallerData();
        if (cda != null) {
            if (cda.length > 1) {
                return Integer.toString(cda[1].getLineNumber());
            }

            if (cda.length == 1) {
                return Integer.toString(cda[0].getLineNumber());
            }
        }

        return "?";
    }
}
