package com.creditease.ns.mq;

public abstract class MQTemplate implements AsyncOperation, SyncOperation, SystemOperation {
    public MQTemplate() {
    }

    public String getActiveQueueName(String requestQueueName) {
        return MQConfig.getConfig.getQueuePrefix() + requestQueueName;
    }
}
