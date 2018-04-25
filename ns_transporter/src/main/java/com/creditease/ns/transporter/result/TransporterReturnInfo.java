package com.creditease.ns.transporter.result;

import com.creditease.framework.scope.RetInfo;

public enum TransporterReturnInfo implements RetInfo {
    UNKNOWN_ERROR("1000", "未知错误");

    private String code;
    private String msg;

    private TransporterReturnInfo(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String toString() {
        return "retCode\":\"" + this.code + "\", \"retInfo\":\"" + this.msg;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
