package com.creditease.ns.log.encoder;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.creditease.ns.log.converter.NewlLineMessageConverter;
import java.io.IOException;

public class NSLogEncoder extends PatternLayoutEncoder {
    public NSLogEncoder() {
    }

    public void doEncode(ILoggingEvent event) throws IOException {
        super.doEncode(event);
    }

    static {
        PatternLayout.defaultConverterMap.put("T", NewlLineMessageConverter.class.getName());
    }
}
