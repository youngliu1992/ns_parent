package com.creditease.ns.transporter.config;

import com.creditease.ns.framework.spring.SpringPlugin;

public class InQueueInfo {
    private String queueName;
    private int bufferSize;
    private int handlerNum;
    private String serviceClassName;
    private String exceptionListenerClassName;
    private SpringPlugin SpringPlugin;
    private String refCatalogId;
    private int fetcherNum;
    private int senderNum;

    public InQueueInfo() {
    }

    public String getQueueName() {
        return this.queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getHandlerNum() {
        return this.handlerNum;
    }

    public void setHandlerNum(int handlerNum) {
        this.handlerNum = handlerNum;
    }

    public String getServiceClassName() {
        return this.serviceClassName;
    }

    public void setServiceClassName(String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }

    public String getExceptionListenerClassName() {
        return this.exceptionListenerClassName;
    }

    public void setExceptionListenerClassName(String exceptionListenerClassName) {
        this.exceptionListenerClassName = exceptionListenerClassName;
    }

    public SpringPlugin getSpringPlugin() {
        return this.SpringPlugin;
    }

    public void setSpringPlugin(SpringPlugin springPlugin) {
        this.SpringPlugin = springPlugin;
    }

    public String getRefCatalogId() {
        return this.refCatalogId;
    }

    public void setRefCatalogId(String refCatalogId) {
        this.refCatalogId = refCatalogId;
    }

    public int getFetcherNum() {
        return this.fetcherNum;
    }

    public void setFetcherNum(int fetcherNum) {
        this.fetcherNum = fetcherNum;
    }

    public int getSenderNum() {
        return this.senderNum;
    }

    public void setSenderNum(int senderNum) {
        this.senderNum = senderNum;
    }
}
