package com.creditease.ns.mq.exception;

public class MQArgumentException extends MQException {
    public MQArgumentException() {
    }

    public MQArgumentException(String message) {
        super(message);
    }

    public MQArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public MQArgumentException(Throwable cause) {
        super(cause);
    }
}