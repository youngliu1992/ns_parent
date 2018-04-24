package com.creditease.ns.log.converter;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.creditease.ns.log.LogKey;

public class NSMessageContentConverter extends MessageConverter {
    public NSMessageContentConverter() {
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
                String module = "";
                if (event.getLoggerName().indexOf(".>") != -1) {
                    module = event.getLoggerName().substring(event.getLoggerName().indexOf(".>") + ".>".length());
                }

                for(int i = 0; i < stringLines.length; ++i) {
                    sb.append(LogKey.getTotalKey());
                    if (i == 0) {
                        if (module != null && !"".equals(module)) {
                            sb.append(" ");
                        }

                        sb.append(module);
                    }

                    sb.append(" - ").append(stringLines[i]);
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
