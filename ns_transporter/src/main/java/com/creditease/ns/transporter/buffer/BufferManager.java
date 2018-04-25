package com.creditease.ns.transporter.buffer;

import com.creditease.framework.pojo.ServiceMessage;

public interface BufferManager {
    ServiceMessage getFromReceiveBuffer(String var1, boolean var2) throws Exception;

    void putInReceiveBuffer(String var1, ServiceMessage var2) throws Exception;

    ServiceMessage getFromSendBuffer(String var1, boolean var2) throws Exception;

    void putInSendBuffer(String var1, ServiceMessage var2) throws Exception;

    long sizeOfReceiveBufferOf(String var1);

    long sizeOfSendBufferOf(String var1);
}
