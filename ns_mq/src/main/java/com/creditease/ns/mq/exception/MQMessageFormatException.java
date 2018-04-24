package com.creditease.ns.mq.exception;

public class MQMessageFormatException extends MQException {
    public MQMessageFormatException() {
    }

    public MQMessageFormatException(String message) {
        super(message);
    }

    public MQMessageFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public MQMessageFormatException(Throwable cause) {
        super(cause);
    }
}