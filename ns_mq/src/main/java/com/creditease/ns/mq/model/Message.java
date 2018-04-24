package com.creditease.ns.mq.model;

import com.creditease.ns.mq.exception.MQConnectionException;
import java.nio.ByteBuffer;

public class Message {
    Header header;
    byte[] body;

    public Message() {
    }

    public Message(String msgId) {
        this.header = new Header(msgId);
    }

    public Message(String msgId, DeliveryMode deliveryMode) {
        this.header = new Header(msgId, deliveryMode);
    }

    public Message(DeliveryMode deliveryMode) {
        this.header = new Header(deliveryMode);
    }

    public Message(byte[] data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        int headLength = byteBuffer.getInt();
        byte[] headerBytes = new byte[headLength];
        byteBuffer.get(headerBytes);
        this.header = Header.toHeader(headerBytes);
        int bodyLength = byteBuffer.getInt();
        this.body = new byte[bodyLength];
        byteBuffer.get(this.body);
    }

    public byte[] toBytes() throws MQConnectionException {
        byte[] headerBytes = Header.toBytes(this.header);
        int arrayLength = 8;
        arrayLength = arrayLength + headerBytes.length;
        if (this.body != null) {
            arrayLength += this.body.length;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(arrayLength);
        byteBuffer.putInt(headerBytes.length);
        byteBuffer.put(headerBytes);
        if (this.body == null) {
            byteBuffer.putInt(-1);
        } else {
            byteBuffer.putInt(this.body.length);
            byteBuffer.put(this.body);
        }

        return byteBuffer.array();
    }

    public Header getHeader() {
        return this.header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public byte[] getBody() {
        return this.body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
