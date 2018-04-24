package com.creditease.ns.mq.exception;

public class MQRedisException extends MQConnectionException {
    public MQRedisException() {
    }

    public MQRedisException(String message) {
        super(message);
    }

    public MQRedisException(String message, Throwable cause) {
        super(message, cause);
    }

    public MQRedisException(Throwable cause) {
        super(cause);
    }
}