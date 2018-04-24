package com.creditease.ns.dispatcher.community.http;

import com.creditease.framework.pojo.DefaultServiceMessage;
import com.creditease.framework.scope.ExchangeScope;
import com.creditease.framework.scope.OutScope;
import com.creditease.framework.scope.RequestScope;
import com.creditease.framework.scope.SystemOutKey;
import com.creditease.framework.scope.SystemRetInfo;
import com.creditease.framework.util.ProtoStuffSerializeUtil;
import com.creditease.ns.dispatcher.core.ConfigCenter;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.MQTemplate;
import com.creditease.ns.mq.MQTemplates;
import com.creditease.ns.mq.model.DeliveryMode;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.mq.model.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class HttpRPCHandler extends ChannelInboundHandlerAdapter
{
    private static NsLog flowLog = NsLog.getFlowLog("Dispatcher", "分发器");
    private static MQTemplate template = MQTemplates.defaultTemplate();

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        if ((msg instanceof HttpContext))
        {
            HttpContext httpContext = (HttpContext)msg;
            NsLog.setMsgId(httpContext.getId());
            String serverName = getURIPath(httpContext.getUri());

            if ((serverName == null) || (serverName.equals(""))) {
                serverName = (String)httpContext.getParams().get("server");
            }
            if (serverName == null) {
                flowLog.info("判断服务名为空，直接返回", new Object[0]);
                writeResponse(ctx.channel(), new ResponseContent(SystemRetInfo.DISP_NOT_FOUND_SERVER_NAME, new HashMap()).toJSON(), HttpResponseStatus.NOT_FOUND, HttpContentType.JSON);
                return;
            }
            flowLog.debug("解析服务名为:{}", new Object[] { serverName });
            Map params = httpContext.getParams();
            if (params == null) {
                params = new HashMap();
            }
            RequestScope scope = new RequestScope(params);
            scope.put("SYSTEM_SERVER_NAME", serverName);

            scope.put("SYSTEM_ALL_PARAMS", params);

            if (httpContext.getQueryString() != null)
            {
                scope.put("SYSTEM_QUERY_STRING", httpContext.getQueryString());
            }
            DefaultServiceMessage bodyMessage = new DefaultServiceMessage(scope);

            Message message = new Message();
            Header header = new Header(httpContext.getId(), DeliveryMode.SYNC);
            header.setServerName(serverName);
            message.setHeader(header);
            message.setBody(ProtoStuffSerializeUtil.serializeForCommon(bodyMessage));
            flowLog.debug("接受请求到准备数据耗时:{}ms", new Object[] { Long.valueOf(System.currentTimeMillis() - httpContext.getAccessTimeStamp()) });
            Message responseMessage = template.publish(ConfigCenter.getConfig.getQueueName(), message);
            long responseStart = System.currentTimeMillis();
            if (responseMessage == null) {
                flowLog.info("返回内容为空", new Object[0]);
                writeResponse(ctx.channel(), new ResponseContent(SystemRetInfo.DISP_NO_RESPONSE).toJSON(), HttpResponseStatus.OK, HttpContentType.JSON);
            } else {
                RequestScope requestScope = new RequestScope();
                ExchangeScope exchangeScope = new ExchangeScope();
                OutScope outScope = new OutScope();
                DefaultServiceMessage defaultServiceMessage = new DefaultServiceMessage(requestScope, exchangeScope, outScope);
                defaultServiceMessage = (DefaultServiceMessage)ProtoStuffSerializeUtil.unSerializeForCommon(responseMessage.getBody());
                if ((responseMessage.getHeader() != null) && (responseMessage.getHeader().getContentType() == 1))
                {
                    String redirectUrl = defaultServiceMessage.getOut(SystemOutKey.HTML_REDIRECT_URL);
                    String windLoadPage = defaultServiceMessage.getOut(SystemOutKey.HTML_WINDOW_ONLOAD);
                    String selfHtmlContent = defaultServiceMessage.getOut(SystemOutKey.HTML_SELF_CONTENT);
                    if (redirectUrl != null) {
                        writeResponseLocation(ctx.channel(), redirectUrl);
                        flowLog.info("HTML 302跳转方式返回", new Object[0]);
                    } else if (windLoadPage != null)
                    {
                        writeResponse(ctx.channel(), windLoadPage, HttpResponseStatus.OK, HttpContentType.HTML);

                        flowLog.info("HTML js reload跳转方式返回", new Object[0]);
                    } else if (selfHtmlContent != null) {
                        writeResponse(ctx.channel(), selfHtmlContent, HttpResponseStatus.OK, HttpContentType.HTML);

                        flowLog.info("HTML 自定义页面返回", new Object[0]);
                    } else {
                        writeResponse(ctx.channel(), "Internal Server Error:跳转地址为空", HttpResponseStatus.INTERNAL_SERVER_ERROR, HttpContentType.HTML);

                        flowLog.error("HTML模式无返回内容", new Object[0]);
                    }
                }
                else {
                    writeResponse(ctx.channel(), defaultServiceMessage.getJsonOut(), HttpResponseStatus.OK, HttpContentType.JSON);

                    flowLog.info("json方式返回:{}", new Object[] { defaultServiceMessage.getJsonOut() });
                }
            }
            flowLog.debug("服务返回到解析数据结束耗时:{}ms", new Object[] { Long.valueOf(System.currentTimeMillis() - responseStart) });
        }
    }

    private void writeResponse(Channel channel, String responseContent, HttpResponseStatus status, HttpContentType contentType) {
        ByteBuf buf = Unpooled.copiedBuffer(responseContent, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf);

        response.headers().set("Content-Type", contentType.toValue());

        response.headers().set("Content-Length", Integer.valueOf(buf.readableBytes()));
        ChannelFuture future = channel.writeAndFlush(response);

        future.addListener(ChannelFutureListener.CLOSE);
    }

    private void writeResponseLocation(Channel channel, String URL)
    {
        boolean close = false;

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);

        response.headers().set("Content-Type", HttpContentType.HTML);
        response.headers().set("Location", URL);
        response.headers().set("Content-Length", Integer.valueOf(0));

        ChannelFuture future = channel.writeAndFlush(response);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception
    {
        ctx.flush();
    }

    private String getURIPath(String uri) {
        if (StringUtils.isNotBlank(uri)) {
            if (uri.contains("?")) {
                int index = uri.indexOf("?");
                uri = uri.substring(0, index);
            }
            String[] urlParts = uri.split("/");

            if (urlParts.length == 2)
                return urlParts[1];
            if (urlParts.length == 3) {
                return urlParts[2];
            }
            return null;
        }

        return null;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        flowLog.error(cause, "处理消息异常");
        writeResponse(ctx.channel(), "{\"retCode\"：500，\"retInfo\"：\"请求的服务超时\"}", HttpResponseStatus.OK, HttpContentType.JSON);
    }
}