package com.creditease.ns.dispatcher.community.log;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.logging.LogLevel;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import java.net.SocketAddress;

public class NSLogginHandler extends ChannelDuplexHandler
{
    private static final LogLevel DEFAULT_LEVEL = LogLevel.DEBUG;

    private static final String NEWLINE = StringUtil.NEWLINE;

    private static final String[] BYTE2HEX = new String[256];
    private static final String[] HEXPADDING = new String[16];
    private static final String[] BYTEPADDING = new String[16];
    private static final char[] BYTE2CHAR = new char[256];
    protected final InternalLogger logger;
    protected final InternalLogLevel internalLevel;
    private final LogLevel level;

    public NSLogginHandler()
    {
        this(DEFAULT_LEVEL);
    }

    public NSLogginHandler(LogLevel level)
    {
        if (level == null) {
            throw new NullPointerException("level");
        }

        this.logger = new NSLogForNetty();
        this.level = level;
        this.internalLevel = InternalLogLevel.INFO;
    }

    public NSLogginHandler(Class<?> clazz)
    {
        this(clazz, DEFAULT_LEVEL);
    }

    public NSLogginHandler(Class<?> clazz, LogLevel level)
    {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        if (level == null) {
            throw new NullPointerException("level");
        }
        this.logger = new NSLogForNetty();
        this.level = level;
        this.internalLevel = InternalLogLevel.INFO;
    }

    public NSLogginHandler(String name)
    {
        this(name, DEFAULT_LEVEL);
    }

    public NSLogginHandler(String name, LogLevel level)
    {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (level == null) {
            throw new NullPointerException("level");
        }
        this.logger = new NSLogForNetty();
        this.level = level;
        this.internalLevel = InternalLogLevel.INFO;
    }

    public LogLevel level()
    {
        return this.level;
    }

    protected String format(ChannelHandlerContext ctx, String message) {
        String chStr = ctx.channel().toString();
        return new StringBuilder(chStr.length() + message.length() + 1).append(chStr).append(' ').append(message).toString();
    }

    public void channelRegistered(ChannelHandlerContext ctx)
            throws Exception
    {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "REGISTERED"));
        }
        super.channelRegistered(ctx);
    }

    public void channelUnregistered(ChannelHandlerContext ctx)
            throws Exception
    {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "UNREGISTERED"));
        }
        super.channelUnregistered(ctx);
    }

    public void channelActive(ChannelHandlerContext ctx)
            throws Exception
    {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "ACTIVE"));
        }
        super.channelActive(ctx);
    }

    public void channelInactive(ChannelHandlerContext ctx)
            throws Exception
    {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "INACTIVE"));
        }
        super.channelInactive(ctx);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception
    {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, new StringBuilder().append("EXCEPTION: ").append(cause).toString()), cause);
        }
        super.exceptionCaught(ctx, cause);
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception
    {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, new StringBuilder().append("USER_EVENT: ").append(evt).toString()));
        }
        super.userEventTriggered(ctx, evt);
    }

    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise)
            throws Exception
    {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, new StringBuilder().append("BIND(").append(localAddress).append(')').toString()));
        }
        super.bind(ctx, localAddress, promise);
    }

    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise)
            throws Exception
    {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, new StringBuilder().append("CONNECT(").append(remoteAddress).append(", ").append(localAddress).append(')').toString()));
        }
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise)
            throws Exception
    {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "DISCONNECT()"));
        }
        super.disconnect(ctx, promise);
    }

    public void close(ChannelHandlerContext ctx, ChannelPromise promise)
            throws Exception
    {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "CLOSE()"));
        }
        super.close(ctx, promise);
    }

    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise)
            throws Exception
    {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "DEREGISTER()"));
        }
        super.deregister(ctx, promise);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        logMessage(ctx, "RECEIVED", msg);
        ctx.fireChannelRead(msg);
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
    {
        logMessage(ctx, "WRITE", msg);
        ctx.write(msg, promise);
    }

    public void flush(ChannelHandlerContext ctx) throws Exception
    {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "FLUSH"));
        }
        ctx.flush();
    }

    private void logMessage(ChannelHandlerContext ctx, String eventName, Object msg) {
        if (this.logger.isEnabled(this.internalLevel))
            this.logger.log(this.internalLevel, format(ctx, formatMessage(eventName, msg)));
    }

    protected String formatMessage(String eventName, Object msg)
    {
        if ((msg instanceof ByteBuf))
            return formatByteBuf(eventName, (ByteBuf)msg);
        if ((msg instanceof ByteBufHolder)) {
            return formatByteBufHolder(eventName, (ByteBufHolder)msg);
        }
        return formatNonByteBuf(eventName, msg);
    }

    protected String formatByteBuf(String eventName, ByteBuf buf)
    {
        int length = buf.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder dump = new StringBuilder(rows * 80 + eventName.length() + 16).append(eventName).append('(').append(length).append('B').append(')').append(new StringBuilder().append(NEWLINE).append("         +-------------------------------------------------+").append(NEWLINE).append("         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |").append(NEWLINE).append("+--------+-------------------------------------------------+----------------+").toString());

        int startIndex = buf.readerIndex();
        int endIndex = buf.writerIndex();

        for (int i = startIndex; i < endIndex; i++) {
            int relIdx = i - startIndex;
            int relIdxMod16 = relIdx & 0xF;
            if (relIdxMod16 == 0) {
                dump.append(NEWLINE).append(Long.toHexString(relIdx & 0xFFFFFFFF | 0x0)).setCharAt(dump.length() - 9, '|');

                dump.append('|');
            }
            dump.append(BYTE2HEX[buf.getUnsignedByte(i)]);
            if (relIdxMod16 == 15) {
                dump.append(" |");
                for (int j = i - 15; j <= i; j++) {
                    dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
                }
                dump.append('|');
            }
        }

        if ((i - startIndex & 0xF) != 0) {
            int remainder = length & 0xF;
            dump.append(HEXPADDING[remainder]).append(" |");

            for (int j = i - remainder; j < i; j++) {
                dump.append(BYTE2CHAR[buf.getUnsignedByte(j)]);
            }
            dump.append(BYTEPADDING[remainder]).append('|');
        }

        dump.append(new StringBuilder().append(NEWLINE).append("+--------+-------------------------------------------------+----------------+").toString());

        return dump.toString();
    }

    protected String formatNonByteBuf(String eventName, Object msg)
    {
        return new StringBuilder().append(eventName).append(": ").append(msg).toString();
    }

    protected String formatByteBufHolder(String eventName, ByteBufHolder msg)
    {
        return formatByteBuf(eventName, msg.content());
    }

    static
    {
        for (int i = 0; i < BYTE2HEX.length; i++) {
            BYTE2HEX[i] = new StringBuilder().append(' ').append(StringUtil.byteToHexStringPadded(i)).toString();
        }

        for (i = 0; i < HEXPADDING.length; i++) {
            int padding = HEXPADDING.length - i;
            StringBuilder buf = new StringBuilder(padding * 3);
            for (int j = 0; j < padding; j++) {
                buf.append("   ");
            }
            HEXPADDING[i] = buf.toString();
        }

        for (i = 0; i < BYTEPADDING.length; i++) {
            int padding = BYTEPADDING.length - i;
            StringBuilder buf = new StringBuilder(padding);
            for (int j = 0; j < padding; j++) {
                buf.append(' ');
            }
            BYTEPADDING[i] = buf.toString();
        }

        for (i = 0; i < BYTE2CHAR.length; i++)
            if ((i <= 31) || (i >= 127))
                BYTE2CHAR[i] = '.';
            else
                BYTE2CHAR[i] = (char)i;
    }
}