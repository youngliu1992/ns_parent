package com.creditease.ns.chains.exchange;

import com.creditease.ns.chains.constants.LoggerConstants;
import com.creditease.ns.log.spi.LoggerWrapper;
import java.util.Map;

public interface Exchanger {
    LoggerWrapper loggerWrapper = LoggerConstants.EXCHANGE_LOGGER;
    String logPrefix = "[Exchanger] ";

    void setExchange(Object var1, Object var2);

    Object getExchange(Object var1);

    void setOut(Object var1, Object var2);

    Object getOut(Object var1);

    Object getParameter(String var1);

    Map getExchangeScope();

    Map getOutScope();
}
