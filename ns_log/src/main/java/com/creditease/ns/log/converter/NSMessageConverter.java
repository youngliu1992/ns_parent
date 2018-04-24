package com.creditease.ns.log.converter;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.creditease.ns.log.LogKey;

public class NSMessageConverter extends MessageConverter {
    public NSMessageConverter() {
    }

    public String convert(ILoggingEvent event) {
        String message = super.convert(event);
        if (message == null) {
            return "";
        } else {
            boolean endWithEnter = message.endsWith("\n");
            String[] stringLines = message.split("\r\n|\n|\r");
            if (stringLines == null) {
                return message;
            } else {
                StringBuilder sb = new StringBuilder();

                for(int i = 0; i < stringLines.length; ++i) {
                    sb.append(LogKey.getTotalKey()).append(" - ").append(stringLines[i]);
                    if (i != stringLines.length - 1) {
                        sb.append("\r\n");
                    } else if (endWithEnter) {
                        sb.append("\r\n");
                    }
                }

                return sb.toString();
            }
        }
    }
}
