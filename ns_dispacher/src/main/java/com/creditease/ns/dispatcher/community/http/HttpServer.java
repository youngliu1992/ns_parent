package com.creditease.ns.dispatcher.community.http;

import com.creditease.ns.dispatcher.community.log.NSLogginHandler;
import com.creditease.ns.dispatcher.core.ConfigCenter;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;

public class HttpServer
        implements LifeCycle
{
    private static NsLog initLog = NsLog.getFramLog("Dispatcher", "分发器");
    private ServerBootstrap bootstrap = new ServerBootstrap();
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup(10);

    public void startUp()
    {
        initLog.info("启动Http服务", new Object[0]);
        int port = ConfigCenter.getConfig.getHttpPort();
        this.bootstrap.option(ChannelOption.SO_BACKLOG, Integer.valueOf(1024));
        ((ServerBootstrap)((ServerBootstrap)this.bootstrap.group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class)).handler(new NSLogginHandler(LogLevel.INFO))).childHandler(new HttpServerInitializer());
        try
        {
            Channel ch = this.bootstrap.bind(port).sync().channel();
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            destroy();
        }
    }

    public void destroy()
    {
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer();
        httpServer.startUp();
    }
}