package com.creditease.ns.dispatcher.community.http;

import com.creditease.framework.scope.RetInfo;
import com.creditease.framework.scope.SystemRetInfo;
import com.creditease.ns.dispatcher.convertor.json.JSONConvertor;
import java.util.Map;

public class ResponseContent
{
    public String retCode;
    public String retInfo;
    public Map data;

    public ResponseContent(RetInfo retCode)
    {
        this.retCode = retCode.getCode();
        this.retInfo = retCode.getMsg();
    }

    public ResponseContent(RetInfo retCode, Map data) {
        this.retCode = retCode.getCode();
        this.retInfo = retCode.getMsg();
        this.data = data;
    }

    public ResponseContent(String retCode, String retInfo) {
        if (retCode == null) {
            retCode = SystemRetInfo.NO_RETURN_CODE.getCode();
        }
        if (retInfo == null) {
            retInfo = SystemRetInfo.NO_RETURN_CODE.getMsg();
        }
        this.retCode = retCode;
        this.retInfo = retInfo;
    }

    public String getRetCode()
    {
        return this.retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetInfo() {
        return this.retInfo;
    }

    public void setRetInfo(String retInfo) {
        this.retInfo = retInfo;
    }

    public Map getData() {
        return this.data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public String toJSON()
    {
        return JSONConvertor.toJSON(this);
    }
}