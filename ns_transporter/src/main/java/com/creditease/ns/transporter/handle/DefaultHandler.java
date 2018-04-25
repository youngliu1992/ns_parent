package com.creditease.ns.transporter.handle;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.exception.StopException;
import com.creditease.framework.pojo.DefaultServiceMessage;
import com.creditease.framework.scope.SystemOutKey;
import com.creditease.framework.util.ExceptionUtil;
import com.creditease.framework.work.Worker;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.transporter.buffer.BufferManager;
import com.creditease.ns.transporter.context.XmlAppTransporterContext;
import com.creditease.ns.transporter.result.TransporterReturnInfo;
import java.lang.reflect.Method;

public class DefaultHandler implements Handler, Runnable {
    private String queueName;
    private BufferManager bufferManager;
    private Object serviceInstance;
    private Method beforeMessageHandleMethod = null;
    private Method messageHandleMethod = null;
    private Method afterMessageHandleMethod = null;
    private boolean isRunning;
    private boolean isStop;
    private XmlAppTransporterContext context;
    private static NsLog frameLog;
    public static NsLog flowLog;

    public DefaultHandler() {
    }

    public void run() {
        if (!this.isRunning) {
            frameLog.debug("{} {} 开始运行", new Object[]{Thread.currentThread().getName(), this.queueName});
            this.isRunning = true;

            while(!this.isStop && !Thread.interrupted()) {
                this.handle();
                NsLog.removeMsgId();
            }
        }

        this.stop();
    }

    public void handle() {
        long startTime = System.currentTimeMillis();
        DefaultServiceMessage serviceMessage = null;
        Header header = null;

        try {
            serviceMessage = (DefaultServiceMessage)this.bufferManager.getFromReceiveBuffer(this.queueName, true);
            header = serviceMessage.getHeader();
            startTime = System.currentTimeMillis();
            flowLog.debug("# 业务层方法调用 queuename:{} {} cost:{}ms #", new Object[]{this.queueName, serviceMessage, System.currentTimeMillis() - startTime});
            ((Worker)this.serviceInstance).doWork(serviceMessage);
            flowLog.info("# 业务层方法调用 OK queuename:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime});
        } catch (InterruptedException var31) {
            flowLog.error("# 业务层方法调用 线程中断 queuename:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var31});
            Thread.currentThread().interrupt();
        } catch (IllegalArgumentException var32) {
            flowLog.error("# 业务层方法调用 失败 queuename:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var32});
            this.sendException(header, var32);
        } catch (StopException var33) {
            flowLog.error("# 业务层方法调用 失败 queuename:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var33});
            this.sendException(header, var33);
            this.setStopFlag(header);
        } catch (NSException var34) {
            flowLog.error("# 业务层方法调用 失败 queuename:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var34});
            this.sendException(header, var34);
            if (serviceMessage != null) {
                try {
                    String outMessage = serviceMessage.getOut(SystemOutKey.RETURN_CODE);
                    if (outMessage == null || outMessage.trim().length() < 1) {
                        serviceMessage.setOut(SystemOutKey.RETURN_CODE, TransporterReturnInfo.UNKNOWN_ERROR);
                    }
                } catch (NSException var30) {
                    var30.printStackTrace();
                }
            }
        } catch (Exception var35) {
            flowLog.error("# 业务层方法调用 失败 queuename:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var35});
            this.sendException(header, var35);
            this.setStopFlag(header);
            if (serviceMessage != null) {
                try {
                    serviceMessage.setOut(SystemOutKey.RETURN_CODE, TransporterReturnInfo.UNKNOWN_ERROR);
                } catch (NSException var29) {
                    var29.printStackTrace();
                }
            }
        } catch (Throwable var36) {
            flowLog.error("# 业务层方法调用 失败 queuename:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var36});
            this.setStopFlag(header);
            this.sendException(header, var36);
            if (serviceMessage != null) {
                try {
                    serviceMessage.setOut(SystemOutKey.RETURN_CODE, TransporterReturnInfo.UNKNOWN_ERROR.toString());
                } catch (NSException var28) {
                    var28.printStackTrace();
                }
            }
        } finally {
            if (serviceMessage != null) {
                try {
                    if (header.getDeliveryMode() == 1) {
                        this.bufferManager.putInSendBuffer(this.queueName, serviceMessage);
                    }
                } catch (Exception var27) {
                    flowLog.error("# 将serviceMessage放入发送缓存 失败 最终消息没有放入发送缓存 queuename:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var27});
                }
            } else {
                flowLog.error("# 将serviceMessage放入发送缓存 失败 最终消息没有放入发送缓存 因为没有拿到serviceMessage queuename:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime});
            }

        }

    }

    private void sendException(Header header, Throwable e) {
        if (header != null) {
            header.setExceptionContent(ExceptionUtil.getStackTrace(e));
        } else {
            flowLog.error("# 将serviceMessage放入发送缓存 记录异常信息 失败 没有得到header queuename:{} #", new Object[]{this.queueName, e});
        }

    }

    private void setStopFlag(Header header) {
        if (header != null) {
            header.setStop();
        } else {
            flowLog.error("# 将serviceMessage放入发送缓存 设置记录标志 失败 没有得到header queuename:{} #", new Object[]{this.queueName});
        }

    }

    public String getQueueName() {
        return this.queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public BufferManager getBufferManager() {
        return this.bufferManager;
    }

    public void setBufferManager(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    public Object getServiceInstance() {
        return this.serviceInstance;
    }

    public void setServiceInstance(Object serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public Method getBeforeMessageHandleMethod() {
        return this.beforeMessageHandleMethod;
    }

    public void setBeforeMessageHandleMethod(Method beforeMessageHandleMethod) {
        this.beforeMessageHandleMethod = beforeMessageHandleMethod;
    }

    public Method getMessageHandleMethod() {
        return this.messageHandleMethod;
    }

    public void setMessageHandleMethod(Method messageHandleMethod) {
        this.messageHandleMethod = messageHandleMethod;
    }

    public Method getAfterMessageHandleMethod() {
        return this.afterMessageHandleMethod;
    }

    public void setAfterMessageHandleMethod(Method afterMessageHandleMethod) {
        this.afterMessageHandleMethod = afterMessageHandleMethod;
    }

    public void stop() {
        this.isStop = true;
        this.isRunning = false;
    }

    public XmlAppTransporterContext getContext() {
        return this.context;
    }

    public void setContext(XmlAppTransporterContext context) {
        this.context = context;
    }

    public void readyShutdown() {
    }

    static {
        frameLog = XmlAppTransporterContext.frameLog;
        flowLog = NsLog.getFlowLog("TransporterFlow", "DefaultHandlerFlow");
    }
}
