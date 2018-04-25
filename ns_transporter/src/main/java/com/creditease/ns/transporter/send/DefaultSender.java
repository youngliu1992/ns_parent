package com.creditease.ns.transporter.send;

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

public class DefaultSender implements Sender, Runnable {
    private String queueName;
    private BufferManager bufferManager;
    private static MQTemplate template = MQTemplates.defaultTemplate();
    private boolean isRunning;
    private boolean isStop;
    private static NsLog frameLog;
    public static NsLog flowLog;

    public DefaultSender() {
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

    public void send() {
        long startTime = System.currentTimeMillis();
        ServiceMessage mqMessage = null;

        try {
            mqMessage = this.bufferManager.getFromSendBuffer(this.queueName, true);
            Message message = MessageConvertUtil.convertToMessage(mqMessage);
            Header header = message.getHeader();
            startTime = System.currentTimeMillis();
            template.reply(message);
            flowLog.info("# 消息响应 成功 queuename:{}  cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime});
        } catch (InterruptedException var6) {
            flowLog.error("# 准备发送消息时 线程中断 应该是线程被中断了 queuename:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var6});
            Thread.currentThread().interrupt();
        } catch (MQException var7) {
            if (var7 instanceof MQArgumentException) {
                flowLog.debug("# 准备发送消息时 失败 底层告知传入参数错误 queueName:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var7});
                return;
            }

            if (var7 instanceof MQConnectionException) {
                flowLog.debug("# 准备发送消息时 失败 底层连接MQ错误 queueName:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var7});
                return;
            }

            if (var7 instanceof MQMessageFormatException) {
                flowLog.debug("# 准备发送消息时 失败 底层告知消息格式出现问题 queueName:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var7});
                return;
            }
        } catch (Exception var8) {
            flowLog.error("# 准备发送消息时 失败 queuename:{} servicemessage:{} cost:{}ms #", new Object[]{this.queueName, System.currentTimeMillis() - startTime, var8});
            return;
        }

        NsLog.removeMsgId();
    }

    public void run() {
        frameLog.debug("# {} {} 开始运行", new Object[]{Thread.currentThread().getName(), this.queueName});
        if (!this.isRunning) {
            this.isRunning = true;

            while(!this.isStop && !Thread.interrupted()) {
                this.send();
            }
        }

        this.stop();
    }

    public void stop() {
        this.isStop = true;
        this.isRunning = false;
    }

    static {
        frameLog = XmlAppTransporterContext.frameLog;
        flowLog = NsLog.getFlowLog("TransporterFlow", "DefaultSenderFlow");
    }
}
