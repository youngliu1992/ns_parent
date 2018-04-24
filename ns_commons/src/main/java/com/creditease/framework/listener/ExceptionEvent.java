package com.creditease.framework.listener;

public class ExceptionEvent implements Event {
    private Throwable exception;

    public ExceptionEvent() {
    }

    public Throwable getException() {
        return this.exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}