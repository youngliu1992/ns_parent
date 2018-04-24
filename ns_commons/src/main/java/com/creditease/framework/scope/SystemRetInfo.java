package com.creditease.framework.scope;

public enum SystemRetInfo implements RetInfo {
    NORMAL("4990", "正常响应"),
    UNKNOWN_ERROR("4999", "未知错误"),
    DISP_NOT_FOUND_SERVER_NAME("4951", "没有传入服务名"),
    DISP_TIME_OUT("4952", "服务请求超时"),
    DISP_NO_RESPONSE("4953", "服务无响应结果"),
    NO_RETURN_CODE("4954", "响应码不存在"),
    CTRL_NOT_FOUND_SEVICE_ERROR("4960", "找不到对应的服务"),
    CTRL_SERVICE_TIMEOUT_ERROR("4961", "服务超时");

    private String code;
    private String msg;

    private SystemRetInfo(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
