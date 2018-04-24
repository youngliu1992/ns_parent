package com.creditease.ns.dispatcher.community.http;

import com.creditease.framework.scope.SystemRetInfo;
import com.creditease.ns.dispatcher.convertor.json.JSONConvertor;
import com.creditease.ns.log.NsLog;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.IncompatibleDataDecoderException;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

public class HttpDispatcherServerHandler extends ChannelInboundHandlerAdapter
{
    private static NsLog flowLog = NsLog.getFlowLog("Dispatcher", "HTTP转换");
    private HttpPostRequestDecoder decoder;
    private static final HttpDataFactory factory = new DefaultHttpDataFactory();
    private HttpRequest httpRequest;

    public void channelUnregistered(ChannelHandlerContext ctx)
            throws Exception
    {
        if (this.decoder != null)
            this.decoder.cleanFiles();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception
    {
        HttpContext httpContext = new HttpContext(UUID.randomUUID().toString());
        if ((msg instanceof HttpRequest)) {
            httpContext.setAccessTimeStamp(System.currentTimeMillis());
            this.httpRequest = ((HttpRequest)msg);
            String requestUri = this.httpRequest.getUri();

            if (requestUri.lastIndexOf("favicon.ico") > 0) {
                writeResponse(ctx.channel(), "");
                return;
            }
            NsLog.setMsgId(httpContext.getId());
            flowLog.info("接收到新请求", new Object[0]);

            httpContext.setUri(requestUri);

            if (this.httpRequest.getMethod() == HttpMethod.GET)
                httpContext.setMethod(HttpRequestMethod.GET);
            else if (this.httpRequest.getMethod() == HttpMethod.POST)
                httpContext.setMethod(HttpRequestMethod.POST);
            else if (this.httpRequest.getMethod() == HttpMethod.DELETE)
                httpContext.setMethod(HttpRequestMethod.DELETE);
            else if (this.httpRequest.getMethod() == HttpMethod.PUT)
                httpContext.setMethod(HttpRequestMethod.PUT);
            else if (this.httpRequest.getMethod() == HttpMethod.HEAD) {
                httpContext.setMethod(HttpRequestMethod.HEAD);
            }

            Map headers = new HashMap();
            HttpHeaders httpHeaders = this.httpRequest.headers();
            if (!httpHeaders.isEmpty()) {
                for (Map.Entry header : httpHeaders)
                {
                    List values = (List)headers.get(header.getKey());
                    if (values == null) {
                        values = new ArrayList();
                        headers.put(header.getKey(), values);
                    }
                    values.add(header.getValue());
                }
            }
            httpContext.setHeaders(headers);
            Map parameters = new HashMap();
            httpContext.setParams(parameters);

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(this.httpRequest.getUri());
            int pathEndPos = this.httpRequest.getUri().indexOf(63);
            if (pathEndPos >= 0) {
                String queryString = queryStringDecoder.uri().substring(queryStringDecoder.path().length() + 1);
                if (queryString != null)
                {
                    httpContext.setQueryString(queryString);
                }
            }

            Map params = queryStringDecoder.parameters();
            if ((params != null) && (params.size() != 0))
                for (Map.Entry entry : params.entrySet()) {
                    key = (String)entry.getKey();
                    for (String value : (List)entry.getValue())
                        parameters.put(key, value);
                }
            String key;
            httpContext.setFromIP(ctx.channel().remoteAddress().toString());
            httpContext.setToIP(ctx.channel().localAddress().toString());
            try
            {
                this.decoder = new HttpPostRequestDecoder(factory, this.httpRequest);
            } catch (HttpPostRequestDecoder.ErrorDataDecoderException e1) {
                e1.printStackTrace();
                dealException(ctx.channel(), e1);
                ctx.channel().close();
                return;
            }
            catch (Exception e1)
            {
                dealException(ctx.channel(), e1);
                return;
            }
        }
        if ((msg instanceof HttpContent)) {
            String contentString = null;
            HttpContent httpContent = (HttpContent)msg;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                contentString = content.toString(CharsetUtil.UTF_8);
                if (StringUtils.isNotBlank(contentString))
                {
                    if (JSONConvertor.isValidJSON(contentString))
                    {
                        Map jsonParameters = JSONConvertor.jsonToMap(contentString);
                        Map parameters = httpContext.getParams();
                        for (Map.Entry<String, String> entry : jsonParameters.entrySet()) {
                            String key = (String)entry.getKey();
                            if (parameters.containsKey(key)) {
                                flowLog.error("请求出现重复Key:{}", new Object[] { key });
                            }
                            httpContext.setParam(key, (String)entry.getValue());
                        }
                        httpContext.setPostContent(contentString);
                    }
                    else {
                        QueryStringDecoder queryStringDecoder = new QueryStringDecoder("/tmp?" + contentString);
                        Map params = queryStringDecoder.parameters();
                        if ((params != null) && (params.size() != 0)) {
                            Map parameters = httpContext.getParams();
                            for (Map.Entry entry : params.entrySet()) {
                                String key = (String)entry.getKey();
                                if (parameters.containsKey(key))
                                    flowLog.error("请求出现重复Key:{}", new Object[] { key });
                                else
                                    for (String value : (List)entry.getValue())
                                        parameters.put(key, value);
                            }
                        }
                    }
                }
            }
        }
        String key;
        flowLog.debug("完整请求参数:{}", new Object[] { httpContext });
        ctx.fireChannelRead(httpContext);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        if ((cause instanceof IOException)) {
            if ((!cause.getMessage().contains("远程主机强迫关闭了一个现有的连接")) && (!cause.getMessage().contains("An existing connection was forcibly closed by the remote host")))
            {
                cause.printStackTrace();
            }
        }
        else cause.printStackTrace();

        ctx.close();
    }

    private void writeResponse(Channel channel, String responseContent)
    {
        writeResponse(channel, responseContent, HttpResponseStatus.OK);
    }

    private void writeResponse(Channel channel, String responseContent, HttpResponseStatus status)
    {
        ByteBuf buf = Unpooled.copiedBuffer(responseContent, CharsetUtil.UTF_8);

        boolean close = false;
        if ((this.httpRequest != null) && (null != this.httpRequest.headers()) && (null != this.httpRequest.headers().get("Connection")) && (null != this.httpRequest.getProtocolVersion())) {
            close = ("close".equalsIgnoreCase(this.httpRequest.headers().get("Connection"))) || ((this.httpRequest.getProtocolVersion().equals(HttpVersion.HTTP_1_0)) && (!"keep-alive".equalsIgnoreCase(this.httpRequest.headers().get("Connection"))));
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);

        response.headers().set("Content-Type", HttpContentType.JSON.toValue());

        if (!close)
        {
            response.headers().set("Content-Length", Integer.valueOf(buf.readableBytes()));
        }

        String value = this.httpRequest.headers().get("Cookie");
        Set cookies;
        if (value == null)
            cookies = Collections.emptySet();
        else {
            cookies = CookieDecoder.decode(value);
        }
        if (!cookies.isEmpty())
        {
            for (Cookie cookie : cookies) {
                response.headers().add("Set-Cookie", ServerCookieEncoder.encode(cookie));
            }
        }

        ChannelFuture future = channel.writeAndFlush(response);

        if (close)
            future.addListener(ChannelFutureListener.CLOSE);
    }

    private void dealException(Channel channel, Throwable ex)
    {
        HttpResponseStatus status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        if (((ex instanceof HttpException)) &&
                (((HttpException)ex).getErrorCode() == 404)) {
            status = HttpResponseStatus.NOT_FOUND;
        }

        flowLog.error(ex, "接受请求异常");
        writeResponse(channel, new ResponseContent(SystemRetInfo.UNKNOWN_ERROR).toJSON(), status);
    }
}