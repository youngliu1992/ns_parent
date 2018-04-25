package com.creditease.ns.transporter.buffer;

import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.transporter.config.InQueueInfo;
import com.creditease.ns.transporter.context.XmlAppTransporterContext;
import com.creditease.ns.transporter.fetch.DefaultFetcher;
import com.creditease.ns.transporter.handle.DefaultHandler;
import com.creditease.ns.transporter.send.DefaultSender;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultBufferManager implements LifeCycle, BufferManager {
    private Map<String, InQueueInfo> queueNameToInQueueInfo;
    private Map<String, BlockingQueue<ServiceMessage>> queueNameToReceiveBuffer;
    private Map<String, BlockingQueue<ServiceMessage>> queueNameToSendBuffer;
    private boolean isStarted = false;
    private static NsLog frameLog;
    private static NsLog handlerLog;
    private static NsLog senderLog;
    private static NsLog fetchLog;
    private static BufferManager self;

    public DefaultBufferManager() {
    }

    public void init() {
        self = this;
    }

    public ServiceMessage getFromReceiveBuffer(String queueName, boolean isSync) throws InterruptedException, Exception {
        long startTime = System.currentTimeMillis();
        ServiceMessage mqMessage = null;
        handlerLog.debug("# 获取接收缓存中的消息对象 queueName:{} receiverbuffersize:{} cost:{}ms #", new Object[]{queueName, ((BlockingQueue)this.queueNameToReceiveBuffer.get(queueName)).size(), System.currentTimeMillis() - startTime});

        try {
            if (isSync) {
                mqMessage = (ServiceMessage)((BlockingQueue)this.queueNameToReceiveBuffer.get(queueName)).take();
            } else {
                mqMessage = (ServiceMessage)((BlockingQueue)this.queueNameToReceiveBuffer.get(queueName)).poll();
            }
        } catch (InterruptedException var7) {
            handlerLog.debug("# 获取接收缓存中对象 失败 线程中断 queueName:{} receiverbuffersize:{} cost:{}ms #", new Object[]{queueName, ((BlockingQueue)this.queueNameToReceiveBuffer.get(queueName)).size(), System.currentTimeMillis() - startTime});
            throw var7;
        }

        NsLog.setMsgId(mqMessage.getHeader().getMessageID());
        handlerLog.trace("# messageHeader:{} #", new Object[]{mqMessage.getHeader()});
        handlerLog.info("# 获取接收缓存中的消息对象 OK queueName:{} receiverbuffersize:{} cost:{}ms #", new Object[]{queueName, ((BlockingQueue)this.queueNameToReceiveBuffer.get(queueName)).size(), System.currentTimeMillis() - startTime});
        return mqMessage;
    }

    public void putInReceiveBuffer(String queueName, ServiceMessage message) throws Exception {
        long startTime = System.currentTimeMillis();
        fetchLog.debug("# 将消息对象放入接收缓存中 queueName:{} receiverbuffersize:{} cost:{}ms #", new Object[]{queueName, ((BlockingQueue)this.queueNameToReceiveBuffer.get(queueName)).size(), System.currentTimeMillis() - startTime});
        ((BlockingQueue)this.queueNameToReceiveBuffer.get(queueName)).put(message);
        fetchLog.trace("# messageHeader:{} #", new Object[]{message.getHeader()});
        fetchLog.info("# 将消息对象放入接收缓存中 OK queueName:{} receiverbuffersize:{} cost:{}ms #", new Object[]{queueName, ((BlockingQueue)this.queueNameToReceiveBuffer.get(queueName)).size(), System.currentTimeMillis() - startTime});
        NsLog.removeMsgId();
    }

    public synchronized void startUp() {
        if (!this.isStarted) {
            this.queueNameToReceiveBuffer = new ConcurrentHashMap();
            this.queueNameToSendBuffer = new ConcurrentHashMap();
            Iterator it = this.queueNameToInQueueInfo.keySet().iterator();

            while(it.hasNext()) {
                String queueName = (String)it.next();
                InQueueInfo inQueueInfo = (InQueueInfo)this.queueNameToInQueueInfo.get(queueName);
                BlockingQueue<ServiceMessage> inBuffer = new LinkedBlockingQueue(inQueueInfo.getBufferSize());
                frameLog.debug("# 消息接受缓存初始化 queuename:{} size:{} #", new Object[]{queueName, inQueueInfo.getBufferSize()});
                this.queueNameToReceiveBuffer.put(queueName, inBuffer);
                BlockingQueue<ServiceMessage> sendBuffer = new LinkedBlockingQueue(inQueueInfo.getBufferSize());
                frameLog.debug("# 消息发送缓存初始化 queuename:{} size:{} #", new Object[]{queueName, inQueueInfo.getBufferSize()});
                this.queueNameToSendBuffer.put(queueName, sendBuffer);
            }

            this.isStarted = true;
        }

    }

    public void destroy() {
    }

    public Map<String, InQueueInfo> getQueueNameToInQueueInfo() {
        return this.queueNameToInQueueInfo;
    }

    public void setQueueNameToInQueueInfo(Map<String, InQueueInfo> queueNameToInQueueInfo) {
        this.queueNameToInQueueInfo = Collections.unmodifiableMap(queueNameToInQueueInfo);
    }

    public Map<String, BlockingQueue<ServiceMessage>> getQueueNameToReceiveBuffer() {
        return this.queueNameToReceiveBuffer;
    }

    public void setQueueNameToReceiveBuffer(Map<String, BlockingQueue<ServiceMessage>> queueNameToBuffer) {
        this.queueNameToReceiveBuffer = queueNameToBuffer;
    }

    public Map<String, BlockingQueue<ServiceMessage>> getQueueNameToSendBuffer() {
        return this.queueNameToSendBuffer;
    }

    public void setQueueNameToSendBuffer(Map<String, BlockingQueue<ServiceMessage>> queueNameToSendBuffer) {
        this.queueNameToSendBuffer = queueNameToSendBuffer;
    }

    public static synchronized BufferManager getInstance() {
        if (self == null) {
            self = new DefaultBufferManager();
        }

        return self;
    }

    public ServiceMessage getFromSendBuffer(String queueName, boolean isSync) throws Exception {
        long startTime = System.currentTimeMillis();
        ServiceMessage serviceMessage = null;

        try {
            if (isSync) {
                serviceMessage = (ServiceMessage)((BlockingQueue)this.queueNameToSendBuffer.get(queueName)).take();
            } else {
                serviceMessage = (ServiceMessage)((BlockingQueue)this.queueNameToSendBuffer.get(queueName)).poll();
            }
        } catch (InterruptedException var7) {
            senderLog.debug("将消息对象从发送缓存中取出 失败 线程中断 queueName:{} sendbuffersize:{} cost:{}ms", new Object[]{queueName, ((BlockingQueue)this.queueNameToSendBuffer.get(queueName)).size(), System.currentTimeMillis() - startTime});
            throw var7;
        } catch (Exception var8) {
            senderLog.debug("将消息对象从发送缓存中取出 失败 queueName:{} t:{} sendbuffersize:{} cost:{}ms", new Object[]{queueName, serviceMessage, ((BlockingQueue)this.queueNameToSendBuffer.get(queueName)).size(), System.currentTimeMillis() - startTime, var8});
            throw var8;
        }

        if (serviceMessage.getHeader() == null) {
            throw new NullPointerException("ServiceMessage");
        } else {
            NsLog.setMsgId(serviceMessage.getHeader().getMessageID());
            senderLog.trace("# messageHeader:{} #", new Object[]{serviceMessage.getHeader()});
            senderLog.info("将消息对象从发送缓存中取出 OK queueName:{} sendbuffersize:{} cost:{}ms", new Object[]{queueName, ((BlockingQueue)this.queueNameToSendBuffer.get(queueName)).size(), System.currentTimeMillis() - startTime});
            return serviceMessage;
        }
    }

    public void putInSendBuffer(String queueName, ServiceMessage message) throws Exception {
        long startTime = System.currentTimeMillis();
        handlerLog.debug("# 将消息对象放入发送缓存 queueName:{} sendbuffersize:{} cost:{}ms #", new Object[]{queueName, ((BlockingQueue)this.queueNameToSendBuffer.get(queueName)).size(), System.currentTimeMillis() - startTime});
        ((BlockingQueue)this.queueNameToSendBuffer.get(queueName)).put(message);
        handlerLog.trace("# messageHeader:{} #", new Object[]{message.getHeader()});
        handlerLog.info("# 将消息对象放入发送缓存 OK queueName:{} sendbuffersize:{} cost:{}ms #", new Object[]{queueName, ((BlockingQueue)this.queueNameToSendBuffer.get(queueName)).size(), System.currentTimeMillis() - startTime});
        NsLog.removeMsgId();
    }

    public long sizeOfReceiveBufferOf(String queueName) {
        return (long)((BlockingQueue)this.queueNameToReceiveBuffer.get(queueName)).size();
    }

    public long sizeOfSendBufferOf(String queueName) {
        return (long)((BlockingQueue)this.queueNameToSendBuffer.get(queueName)).size();
    }

    static {
        frameLog = XmlAppTransporterContext.frameLog;
        handlerLog = DefaultHandler.flowLog;
        senderLog = DefaultSender.flowLog;
        fetchLog = DefaultFetcher.flowLog;
    }
}
