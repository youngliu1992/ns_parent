package com.creditease.ns.mq.exception;

public class MQInitError extends RuntimeException {
    public MQInitError() {
    }

    public MQInitError(String message) {
        super(message);
    }

    public MQInitError(String message, Throwable cause) {
        super(message, cause);
    }

    public MQInitError(Throwable cause) {
        super(cause);
    }

    protected MQInitError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}