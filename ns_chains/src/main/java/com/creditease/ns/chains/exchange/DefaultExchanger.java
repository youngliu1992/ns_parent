package com.creditease.ns.chains.exchange;

import com.creditease.ns.log.NsLog;
import java.util.HashMap;
import java.util.Map;

public class DefaultExchanger implements Exchanger {
    private static NsLog flowLog = NsLog.getFlowLog("链内部传递对象问题查看", "主要用作调试");
    private Map<String, Object> requestScope;
    private Map<Object, Object> exchangeScope;
    private Map<Object, Object> outScope;

    public void setExchange(Object key, Object value) {
        this.exchangeScope.put(key, value);
        flowLog.trace("[Exchanger] [放入交换域] [成功] [key:{}] [value:{}] [{}] [{}]", new Object[]{key, value, this.exchangeScope.size(), this.exchangeScope});
    }

    public Object getExchange(Object key) {
        Object value = this.exchangeScope.get(key);
        flowLog.trace("[Exchanger] [得到交换域值] [成功] [key:{}] [value:{}] [{}]", new Object[]{key, value, this.exchangeScope.size(), this.exchangeScope});
        return value;
    }

    public void setOut(Object key, Object value) {
        this.outScope.put(key, value);
        flowLog.trace("[Exchanger] [放入输出域] [成功] [key:{}] [value:{}] [{}] [{}]", new Object[]{key, value, this.outScope.size(), this.outScope});
    }

    public Object getOut(Object key) {
        Object value = this.outScope.get(key);
        flowLog.trace("[Exchanger] [得到输出域值] [成功] [key:{}] [value:{}] [{}] [{}]", new Object[]{key, value, this.outScope.size(), this.outScope});
        return value;
    }

    public DefaultExchanger(Map<String, Object> requestScope) {
        this.requestScope = requestScope;
        this.exchangeScope = new HashMap();
        this.outScope = new HashMap();
    }

    public Object getParameter(String key) {
        Object value = this.requestScope.get(key);
        flowLog.trace("[Exchanger] [得到输出域值] [成功] [key:{}] [value:{}] [{}] [{}]", new Object[]{key, value, this.requestScope.size(), this.requestScope});
        return value;
    }

    public Map getExchangeScope() {
        return this.exchangeScope;
    }

    public Map getOutScope() {
        return this.outScope;
    }

    public String toString() {
        return "[" + this.requestScope + "] [" + this.exchangeScope + "] [" + this.outScope + "]";
    }
}
