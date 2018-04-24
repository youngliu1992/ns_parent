package com.creditease.ns.dispatcher.community.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class HttpContext
        implements Serializable
{
    private String id;
    private long accessTimeStamp;
    private String uri;
    private HttpRequestMethod method;
    private String postContent;
    private String fromIP;
    private String toIP;
    private Map<String, List<String>> headers;
    private Map<String, String> params;
    private String queryString;
    private String responseContent;
    private HttpResponseStatus responseStatus;
    private HttpContentType responseContentType;

    public HttpContext(String id)
    {
        this.id = id;
    }

    public String getUri()
    {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public HttpRequestMethod getMethod() {
        return this.method;
    }

    public void setMethod(HttpRequestMethod method) {
        this.method = method;
    }

    public String getPostContent() {
        return this.postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getFromIP() {
        return this.fromIP;
    }

    public void setFromIP(String fromIP) {
        this.fromIP = fromIP;
    }

    public String getToIP() {
        return this.toIP;
    }

    public void setToIP(String toIP) {
        this.toIP = toIP;
    }

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public HttpContentType getResponseContentType() {
        return this.responseContentType;
    }

    public void setResponseContentType(HttpContentType responseContentType) {
        this.responseContentType = responseContentType;
    }

    public String getResponseContent() {
        return this.responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public HttpResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    public void setResponseStatus(HttpResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public long getAccessTimeStamp() {
        return this.accessTimeStamp;
    }

    public void setAccessTimeStamp(long accessTimeStamp) {
        this.accessTimeStamp = accessTimeStamp;
    }

    public void setParam(String key, String value) {
        Map params = getParams();
        if (params == null) {
            params = new HashMap();
            setParams(params);
        }
        params.put(key, value);
    }

    public String getId() {
        return this.id;
    }

    public String getQueryString()
    {
        return this.queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}