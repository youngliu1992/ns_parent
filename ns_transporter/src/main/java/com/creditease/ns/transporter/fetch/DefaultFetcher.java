package com.creditease.ns.transporter.fetch;

import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.util.MessageConvertUtil;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.MQTemplate;
import com.creditease.ns.mq.MQTemplates;
import com.creditease.ns.mq.exception.MQArgumentException;
import com.creditease.ns.mq.exception.MQConnectionException;
import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.exception.MQMessageFormatException;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.mq.model.Message;
import com.creditease.ns.transporter.buffer.BufferManager;
import com.creditease.ns.transporter.context.XmlAppTransporterContext;
import java.io.UnsupportedEncodingException;

public class DefaultFetcher implements Fetcher, Runnable {
    private String queueName;
    private BufferManager bufferManager;
    private static MQTemplate template = MQTemplates.defaultTemplate();
    private boolean isRunning;
    private boolean isStop;
    private static final int RETRY_NUM = 3;
    private static NsLog frameLog;
    public static NsLog flowLog;

    public DefaultFetcher() {
    }

    public void run() {
        frameLog.debug("{} {} 开始运行", new Object[]{Thread.currentThread().getName(), this.queueName});
        if (!this.isRunning) {
            this.isRunning = true;

            while(!this.isStop && !Thread.interrupted()) {
                this.fetch();
            }

            frameLog.debug("{} {} 正在停止运行 isrunning:{} isstop:{}", new Object[]{Thread.currentThread().getName(), this.queueName, this.isRunning, this.isStop});
        }

        this.stop();
        frameLog.debug("{} {} 停止运行 isrunning:{} isstop:{}", new Object[]{Thread.currentThread().getName(), this.queueName, this.isRunning, this.isStop});
    }

    public void fetch() {
        long startTime = System.currentTimeMillis();
        Message message = null;

        try {
            message = template.receive(this.queueName);
            NsLog.setMsgId(message.getHeader().getMessageID());
        } catch (MQException var9) {
            if (var9 instanceof MQArgumentException) {
                flowLog.error("# 从MQ中接收消息 失败 传给MQ的参数错误 queueName:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var9});
                return;
            }

            if (var9 instanceof MQConnectionException) {
                flowLog.error("# 从MQ中接收消息 失败 连接MQ失败 queueName:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var9});
                return;
            }

            if (var9 instanceof MQMessageFormatException) {
                flowLog.error("# 从MQ中接收消息 失败 传给MQ的消息格式不符合规范 queueName:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var9});
                return;
            }
        } catch (Throwable var10) {
            flowLog.error("# 从MQ中接收消息 失败 出现未知异常 queueName:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var10});
            return;
        }

        if (message == null) {
            flowLog.error("# 从MQ中接收消息 失败 没有返回消息 queueName:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime});
        } else {
            Header header = message.getHeader();
            flowLog.debug("header:{}", new Object[]{header});
            flowLog.info("# 从MQ中接收消息 OK queueName:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime});

            try {
                ServiceMessage serviceMessage = MessageConvertUtil.convertToServiceMessage(message);
                startTime = System.currentTimeMillis();
                this.bufferManager.putInReceiveBuffer(this.queueName, serviceMessage);
            } catch (UnsupportedEncodingException var7) {
                flowLog.error("# 将消息放入接收缓存 失败 编码不支持 queueName:{} header:{} cost:{}ms #", new Object[]{this.queueName, header, System.currentTimeMillis() - startTime, var7});
            } catch (Exception var8) {
                flowLog.error("# 将消息放入接收缓存 失败 出现未知异常 queueName:{} header:{} cost:{}ms #", new Object[]{this.queueName, header, System.currentTimeMillis() - startTime, var8});
            }

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

    public void stop() {
        this.queueName = null;
        this.bufferManager = null;
        this.isStop = true;
        this.isRunning = false;
    }

    static {
        frameLog = XmlAppTransporterContext.frameLog;
        flowLog = NsLog.getFlowLog("TransporterFlow", "DefaultFetcherFlow");
    }
}
