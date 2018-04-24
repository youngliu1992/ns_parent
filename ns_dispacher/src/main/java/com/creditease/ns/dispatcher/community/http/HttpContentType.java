package com.creditease.ns.dispatcher.community.http;

public enum HttpContentType
{
    TEXT("text/plain;charset=UTF-8"),
    HTML("text/html;charset=UTF-8"),
    XML("text/xml"),
    JSON("application/json;charset=UTF-8");

    private final String value;

    private HttpContentType(String value) { this.value = value; }

    public String toValue() {
        return this.value;
    }
}