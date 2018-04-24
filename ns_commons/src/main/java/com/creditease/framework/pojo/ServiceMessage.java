package com.creditease.framework.pojo;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.scope.ExchangeKey;
import com.creditease.framework.scope.OutKey;
import com.creditease.ns.mq.model.Header;
import java.util.Map;

public interface ServiceMessage {
    Header getHeader();

    void setHeader(Header var1) throws NSException;

    <T> T getParameterByType(String var1, Class<T> var2);

    String getParameter(String var1);

    void setExchange(ExchangeKey var1, Object var2) throws NSException;

    String getExchange(ExchangeKey var1) throws NSException;

    <T> T getExchangeByType(ExchangeKey var1, Class<T> var2) throws NSException;

    void setOut(OutKey var1, Object var2) throws NSException;

    String getOut(OutKey var1) throws NSException;

    <T> T getOutByType(OutKey var1, Class<T> var2) throws NSException;

    void clearAllOut() throws NSException;

    void removeOut(OutKey var1) throws NSException;

    String getJsonOut() throws NSException;

    void setOutHtmlAsRedirect(String var1) throws NSException;

    void setOutHtmlAsWinOnload(Map<String, String> var1, String var2) throws NSException;

    void setOutHtmlConent(String var1) throws NSException;
}
