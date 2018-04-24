package com.creditease.framework.pojo;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.scope.ExchangeKey;
import com.creditease.framework.scope.ExchangeScope;
import com.creditease.framework.scope.OutKey;
import com.creditease.framework.scope.OutScope;
import com.creditease.framework.scope.RequestScope;
import com.creditease.framework.scope.RetInfo;
import com.creditease.framework.scope.SystemOutKey;
import com.creditease.framework.scope.SystemRetInfo;
import com.creditease.framework.util.JsonUtil;
import com.creditease.framework.util.ProtoStuffSerializeUtil;
import com.creditease.ns.mq.model.Header;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DefaultServiceMessage implements ServiceMessage {
    private Header header;
    private final RequestScope requestScope;
    private final ExchangeScope exchangeScope;
    private final OutScope outScope;

    public Header getHeader() {
        return this.header;
    }

    public void setHeader(Header header) throws NSException {
        this.header = header;
    }

    public String getParameter(String paramName) {
        return (String)this.requestScope.get(paramName);
    }

    public <T> T getParameterByType(String paramName, Class<T> clazz) {
        return clazz.cast(this.requestScope.get(paramName));
    }

    public void setExchange(ExchangeKey key, Object value) throws NSException {
        try {
            this.checkExchangeKey(key);
        } catch (Exception var5) {
            throw new NSException(" [放入Exchange域值] [失败] [" + key + "] [" + value + "]", var5);
        }

        try {
            this.exchangeScope.put(key.toString(), ProtoStuffSerializeUtil.serializeForCommon(value));
        } catch (Exception var4) {
            throw new NSException("[放入Exchange域值] [对象转换protobuffer数据失败] [" + key + "] [" + value + "]", var4);
        }
    }

    public String getExchange(ExchangeKey key) throws NSException {
        return (String)this.getExchangeByType(key, String.class);
    }

    public <T> T getExchangeByType(ExchangeKey key, Class<T> clazz) throws NSException {
        try {
            this.checkExchangeKey(key);
        } catch (Exception var6) {
            throw new NSException(" [获取Exchange域值] [失败] [" + key + "] [" + clazz + "]", var6);
        }

        byte[] contentbytes = (byte[])((byte[])this.exchangeScope.get(key.toString()));
        if (contentbytes == null) {
            return null;
        } else {
            try {
                //modify by liuy
                return (T)ProtoStuffSerializeUtil.unSerializeForCommon(contentbytes);
            } catch (Exception var5) {
                throw new NSException("[获取Exchange域值] [protobuff转换对象失败] [" + key + "] [" + clazz + "] [" + contentbytes.length + "]", var5);
            }
        }
    }

    public void setOut(OutKey outKey, Object value) throws NSException {
        try {
            this.checkOutKey(outKey);
        } catch (Exception var5) {
            throw new NSException(" [放入Out域] [失败] [" + outKey + "] [" + value + "]", var5);
        }

        try {
            if (value instanceof RetInfo) {
                RetInfo retInfo = (RetInfo)value;
                this.outScope.put(outKey.toString(), JsonUtil.jsonFromObject("\"retCode\":\"" + retInfo.getCode() + "\", \"retInfo\":\"" + retInfo.getMsg() + "\""));
            } else {
                this.outScope.put(outKey.toString(), JsonUtil.jsonFromObject(value));
            }

        } catch (Exception var4) {
            throw new NSException("[放入Out域] [对象转换json数据失败] [" + outKey + "] [" + value + "]", var4);
        }
    }

    public String getOut(OutKey outKey) throws NSException {
        return (String)this.getOutByType(outKey, String.class);
    }

    public <T> T getOutByType(OutKey outKey, Class<T> clazz) throws NSException {
        try {
            this.checkOutKey(outKey);
        } catch (Exception var6) {
            throw new NSException(" [获取Out域值] [失败] [" + outKey + "] [" + clazz + "]", var6);
        }

        String json = (String)this.outScope.get(outKey.toString());
        if (json == null) {
            return null;
        } else {
            try {
                return JsonUtil.objectFromJson(json, clazz);
            } catch (Exception var5) {
                throw new NSException(" [获取Out域值] [json转对象失败] [" + outKey + "] [" + clazz + "][" + json + "]", var5);
            }
        }
    }

    public String toString() {
        return "messageheader:" + this.header + " requestScope:" + this.requestScope + " exchangeScope:" + this.exchangeScope + " outScope:" + this.outScope + "";
    }

    private void checkExchangeKey(ExchangeKey key) throws Exception {
        if (key == null) {
            throw new NullPointerException("不接受null值作为key");
        } else if (!key.getClass().isEnum()) {
            throw new IllegalArgumentException("传入的ExchangeKey" + key + "不是Enum");
        }
    }

    private void checkOutKey(OutKey key) throws Exception {
        if (key == null) {
            throw new NullPointerException("不接受null值作为key");
        } else if (!key.getClass().isEnum()) {
            throw new IllegalArgumentException("传入的OutKey" + key + "不是Enum");
        }
    }

    public DefaultServiceMessage(RequestScope requestScope) {
        this.requestScope = requestScope;
        this.exchangeScope = new ExchangeScope();
        this.outScope = new OutScope();
    }

    public DefaultServiceMessage(RequestScope requestScope, ExchangeScope exchangeScope, OutScope outScope) {
        this.requestScope = requestScope;
        this.exchangeScope = exchangeScope;
        this.outScope = outScope;
    }

    public String getJsonOut() throws NSException {
        StringBuilder jsonBuilder = new StringBuilder();
        if (this.outScope != null && !this.outScope.isEmpty()) {
            jsonBuilder.append("{");
            String resultInfo = this.getOut(SystemOutKey.RETURN_CODE);
            if (resultInfo != null) {
                jsonBuilder.append(resultInfo);
            } else {
                jsonBuilder.append("\"retCode\"：\"" + SystemRetInfo.NO_RETURN_CODE.getCode() + "\"，\"retInfo\"：\"" + SystemRetInfo.NO_RETURN_CODE.getMsg() + "\"");
            }

            jsonBuilder.append(",\"data\":{");
            Iterator i$ = this.outScope.entrySet().iterator();

            while(i$.hasNext()) {
                Entry<String, String> entry = (Entry)i$.next();
                if (!SystemOutKey.RETURN_CODE.toString().equals(entry.getKey())) {
                    jsonBuilder.append("\"").append((String)entry.getKey()).append("\"").append(":").append((String)entry.getValue()).append(",");
                }
            }

            if (jsonBuilder.charAt(jsonBuilder.length() - 1) == ',') {
                jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
            }

            jsonBuilder.append("}}");
        }

        return jsonBuilder.toString();
    }

    public void setOutHtmlAsRedirect(String url) throws NSException {
        this.setOut(SystemOutKey.HTML_REDIRECT_URL, url);
    }

    public void setOutHtmlAsWinOnload(Map<String, String> form, String url) throws NSException {
        StringBuilder sb = new StringBuilder("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        sb.append("<html><body>").append("<form id=\"payBillForm\" action=\"").append(url).append("\" method=\"post\">");
        Iterator i$ = form.entrySet().iterator();

        while(i$.hasNext()) {
            Entry<String, String> param = (Entry)i$.next();
            sb.append("<input type=\"hidden\" name=\"").append((String)param.getKey()).append("\" value=\"").append((String)param.getValue()).append("\"/>");
        }

        sb.append("</form></body>");
        sb.append("<script language=\"javascript\" type=\"text/javascript\"> window.onload=function(){ document.getElementById(\"payBillForm\").submit();}</script></html>");
        this.setOut(SystemOutKey.HTML_WINDOW_ONLOAD, sb.toString());
    }

    public void setOutHtmlConent(String htmlContent) throws NSException {
        this.setOut(SystemOutKey.HTML_SELF_CONTENT, htmlContent);
    }

    public void clearAllOut() throws NSException {
        this.outScope.clear();
    }

    public void removeOut(OutKey outKey) throws NSException {
        try {
            this.checkOutKey(outKey);
        } catch (Exception var4) {
            throw new NSException(" [删除Out域] [失败] [" + outKey + "] [", var4);
        }

        try {
            this.outScope.remove(outKey.toString());
        } catch (Exception var3) {
            throw new NSException("[删除Out域] [失败] [" + outKey + "]", var3);
        }
    }
}
