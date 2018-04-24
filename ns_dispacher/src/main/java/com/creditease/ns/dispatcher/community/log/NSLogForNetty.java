package com.creditease.ns.dispatcher.community.log;

import com.creditease.ns.log.NsLog;
import io.netty.util.internal.logging.AbstractInternalLogger;

class NSLogForNetty extends AbstractInternalLogger
{
    private static final long serialVersionUID = 108038972685130825L;
    private final transient NsLog logger;

    NSLogForNetty()
    {
        super("Netty框架");
        this.logger = NsLog.getFramLog("Dispatcher", "Netty框架");
    }

    public boolean isTraceEnabled()
    {
        return this.logger.isDebugEnabled();
    }

    public void trace(String msg)
    {
        this.logger.debug(msg, new Object[0]);
    }

    public void trace(String format, Object arg)
    {
        this.logger.debug(format, new Object[] { arg });
    }

    public void trace(String format, Object argA, Object argB)
    {
        this.logger.debug(format, new Object[] { argA, argB });
    }

    public void trace(String format, Object[] argArray)
    {
        this.logger.debug(format, argArray);
    }

    public void trace(String msg, Throwable t)
    {
        this.logger.debug(msg, new Object[] { t });
    }

    public boolean isDebugEnabled()
    {
        return this.logger.isDebugEnabled();
    }

    public void debug(String msg)
    {
        this.logger.debug(msg, new Object[0]);
    }

    public void debug(String format, Object arg)
    {
        this.logger.debug(format, new Object[] { arg });
    }

    public void debug(String format, Object argA, Object argB)
    {
        this.logger.debug(format, new Object[] { argA, argB });
    }

    public void debug(String format, Object[] argArray)
    {
        this.logger.debug(format, argArray);
    }

    public void debug(String msg, Throwable t)
    {
        this.logger.debug(msg, new Object[] { t });
    }

    public boolean isInfoEnabled()
    {
        return this.logger.isInfoEnabled();
    }

    public void info(String msg)
    {
        this.logger.info(msg, new Object[0]);
    }

    public void info(String format, Object arg)
    {
        this.logger.info(format, new Object[] { arg });
    }

    public void info(String format, Object argA, Object argB)
    {
        this.logger.info(format, new Object[] { argA, argB });
    }

    public void info(String format, Object[] argArray)
    {
        this.logger.info(format, argArray);
    }

    public void info(String msg, Throwable t)
    {
        this.logger.info(msg, new Object[] { t });
    }

    public boolean isWarnEnabled()
    {
        return this.logger.isWarnEnabled();
    }

    public void warn(String msg)
    {
        this.logger.warn(msg, new Object[0]);
    }

    public void warn(String format, Object arg)
    {
        this.logger.warn(format, new Object[] { arg });
    }

    public void warn(String format, Object[] argArray)
    {
        this.logger.warn(format, argArray);
    }

    public void warn(String format, Object argA, Object argB)
    {
        this.logger.warn(format, new Object[] { argA, argB });
    }

    public void warn(String msg, Throwable t)
    {
        this.logger.warn(msg, new Object[] { t });
    }

    public boolean isErrorEnabled()
    {
        return this.logger.isErrorEnabled();
    }

    public void error(String msg)
    {
        this.logger.error(msg, new Object[0]);
    }

    public void error(String format, Object arg)
    {
        this.logger.error(format, new Object[] { arg });
    }

    public void error(String format, Object argA, Object argB)
    {
        this.logger.error(format, new Object[] { argA, argB });
    }

    public void error(String format, Object[] argArray)
    {
        this.logger.error(format, argArray);
    }

    public void error(String msg, Throwable t)
    {
        this.logger.error(msg, t);
    }
}