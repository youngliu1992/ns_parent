package com.creditease.ns.dispatcher.community.http;

import java.io.Serializable;

public class HttpException extends Exception
        implements Serializable
{
    private String errorMsg;
    private int errorCode;

    public HttpException(String errorMsg, int errorCode, Throwable cause)
    {
        super(errorMsg, cause);
        setErrorMsg(errorMsg);
        setErrorCode(errorCode);
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}