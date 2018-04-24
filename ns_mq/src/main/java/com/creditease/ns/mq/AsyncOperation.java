package com.creditease.ns.mq;

import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.model.Message;

public interface AsyncOperation {
    void send(String var1, byte[] var2) throws MQException;

    void send(String var1, String var2, byte[] var3) throws MQException;

    void send(String var1, Message var2) throws MQException;

    Message receive(String var1) throws MQException;

    Message receive(String var1, int var2) throws MQException;

    Message receiveNoneBlock(String var1) throws MQException;
}
