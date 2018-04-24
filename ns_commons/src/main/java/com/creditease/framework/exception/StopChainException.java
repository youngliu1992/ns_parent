package com.creditease.framework.exception;

public class StopChainException extends StopException {
    public StopChainException() {
    }

    public StopChainException(String message) {
        super(message);
    }

    public StopChainException(String message, Throwable cause) {
        super(message, cause);
    }

    public StopChainException(Throwable cause) {
        super(cause);
    }

    public StopChainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}