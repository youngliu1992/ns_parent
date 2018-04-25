package com.creditease.ns.transporter.chain.adapter;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.ExchangeKey;
import com.creditease.framework.scope.OutKey;
import com.creditease.framework.scope.OutScope;
import com.creditease.framework.scope.RequestScope;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.mq.model.Header;
import java.util.Map;

public class ServiceMessageExchangerAdapter implements ServiceMessage {
    private ServiceMessage serviceMessage;
    private RequestScope requestScope;
    private Exchanger exchanger;
    private OutScope outScope;
    private final String logPrefix = "[ServiceMessageExchangerAdapter]";

    public ServiceMessageExchangerAdapter() {
    }

    public Header getHeader() {
        return this.serviceMessage.getHeader();
    }

    public void setHeader(Header header) throws NSException {
    }

    public <T> T getParameterByType(String paramName, Class<T> clazz) {
        return this.serviceMessage.getParameterByType(paramName, clazz);
    }

    public String getParameter(String paramName) {
        return this.serviceMessage.getParameter(paramName);
    }

    public void setExchange(ExchangeKey key, Object value) throws NSException {
        this.doSetExchange(key, value);
        if (!(key instanceof Abandonable)) {
            this.serviceMessage.setExchange(key, value);
        }

    }

    public String getExchange(ExchangeKey key) throws NSException {
        String exchangerResult = (String)this.exchanger.getExchange(key);
        if (exchangerResult == null && !(key instanceof Abandonable)) {
            String s = this.serviceMessage.getExchange(key);
            this.doSetExchange(key, s);
            return s;
        } else {
            return exchangerResult;
        }
    }

    public <T> T getExchangeByType(ExchangeKey key, Class<T> clazz) throws NSException {
        Object object = this.exchanger.getExchange(key);
        if (object == null && !(key instanceof Abandonable)) {
            object = this.serviceMessage.getExchangeByType(key, clazz);
            this.doSetExchange(key, object);
        }

        return object;
    }

    public void setOut(OutKey outKey, Object value) throws NSException {
        this.serviceMessage.setOut(outKey, value);
    }

    public String getOut(OutKey outKey) throws NSException {
        return this.serviceMessage.getOut(outKey);
    }

    public <T> T getOutByType(OutKey outKey, Class<T> clazz) throws NSException {
        return this.serviceMessage.getOutByType(outKey, clazz);
    }

    public String getJsonOut() throws NSException {
        return this.serviceMessage.getJsonOut();
    }

    public void setOutHtmlAsRedirect(String url) throws NSException {
        this.serviceMessage.setOutHtmlAsRedirect(url);
    }

    public void setOutHtmlAsWinOnload(Map<String, String> form, String url) throws NSException {
        this.serviceMessage.setOutHtmlAsWinOnload(form, url);
    }

    public ServiceMessage getServiceMessage() {
        return this.serviceMessage;
    }

    public void setServiceMessage(ServiceMessage serviceMessage) {
        this.serviceMessage = serviceMessage;
    }

    public RequestScope getRequestScope() {
        return this.requestScope;
    }

    public void setRequestScope(RequestScope requestScope) {
        this.requestScope = requestScope;
    }

    public Exchanger getExchanger() {
        return this.exchanger;
    }

    public void setExchanger(Exchanger exchanger) {
        this.exchanger = exchanger;
    }

    public OutScope getOutScope() {
        return this.outScope;
    }

    public void setOutScope(OutScope outScope) {
        this.outScope = outScope;
    }

    public void clearAllOut() throws NSException {
        this.serviceMessage.clearAllOut();
    }

    public void removeOut(OutKey outKey) throws NSException {
        this.serviceMessage.removeOut(outKey);
    }

    private void doSetExchange(ExchangeKey key, Object value) {
        this.exchanger.setExchange(key, value);
        this.exchanger.setExchange(key.toString(), value);
    }

    public void setOutHtmlConent(String htmlContent) throws NSException {
        this.serviceMessage.setOutHtmlConent(htmlContent);
    }
}
