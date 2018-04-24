package com.creditease.ns.mq;

import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.model.Message;

public interface SyncOperation {
    Message publish(String var1, byte[] var2) throws MQException;

    Message publish(String var1, byte[] var2, int var3) throws MQException;

    Message publish(String var1, byte[] var2, String var3, int var4) throws MQException;

    Message publish(String var1, Message var2) throws MQException;

    Message publish(String var1, Message var2, int var3) throws MQException;

    void reply(Message var1) throws MQException;
}
