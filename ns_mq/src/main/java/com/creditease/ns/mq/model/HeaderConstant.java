package com.creditease.ns.mq.model;

public class HeaderConstant {
    public static final int DELIVERY_MODE_SYNC = 1;
    public static final int DELIVERY_MODE_ASYNC = 2;
    public static final int CONTENT_TYPE_HTML = 1;
    public static final int CONTENT_TYPE_JSON = 2;
    public static final int CONTENT_ENCODING_UTF_8 = 1;
    public static final int CHAIN_CONTINUE = 1;
    public static final int CHAIN_STOPABLE = 2;

    private HeaderConstant() {
    }
}