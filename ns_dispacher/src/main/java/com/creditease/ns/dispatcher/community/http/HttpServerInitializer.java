package com.creditease.ns.dispatcher.community.http;

import com.creditease.ns.dispatcher.core.ConfigCenter;
import com.creditease.ns.log.NsLog;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel>
{
    private static NsLog initLog = NsLog.getFramLog("Dispatcher", "分发器");
    DefaultEventExecutorGroup ioExecutorGroup = new DefaultEventExecutorGroup(ConfigCenter.getConfig.getDispatcherPoolNum());

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline p = socketChannel.pipeline();
        p.addLast(new ChannelHandler[] { new HttpServerCodec(4096, 8192, 8192, false) });
        p.addLast(new ChannelHandler[] { new HttpObjectAggregator(1048576) });
        p.addLast(new ChannelHandler[] { new HttpDispatcherServerHandler() });
        p.addLast(this.ioExecutorGroup, new ChannelHandler[] { new HttpRPCHandler() });
    }
}