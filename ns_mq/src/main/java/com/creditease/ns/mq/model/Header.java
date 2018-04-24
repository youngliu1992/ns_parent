package com.creditease.ns.mq.model;

import com.creditease.ns.mq.model.serialize.StringSerialize;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.UUID;

public class Header {
    int version = 2;
    int deliveryMode;
    int contentType = 2;
    int contentEncoding = 1;
    int stopable = 1;
    String replyTo;
    String correlationID;
    String messageID;
    String serverName;
    String exceptionContent;

    public Header() {
        this.messageID = UUID.randomUUID().toString();
    }

    public Header(String msgId) {
        this.messageID = msgId;
    }

    public Header(String msgId, DeliveryMode mode) {
        this.messageID = msgId;
        if (mode == DeliveryMode.SYNC) {
            this.replyTo = UUID.randomUUID().toString();
            this.correlationID = UUID.randomUUID().toString();
            this.deliveryMode = 1;
        } else {
            this.deliveryMode = 2;
        }

    }

    public Header(DeliveryMode mode) {
        this.messageID = UUID.randomUUID().toString();
        if (mode == DeliveryMode.SYNC) {
            this.replyTo = UUID.randomUUID().toString();
            this.correlationID = UUID.randomUUID().toString();
            this.deliveryMode = 1;
        } else {
            this.deliveryMode = 2;
        }

    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getReplyTo() {
        return this.replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getCorrelationID() {
        return this.correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    public String getMessageID() {
        return this.messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public int getContentType() {
        return this.contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getContentEncoding() {
        return this.contentEncoding;
    }

    public void setContentEncoding(int contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public int getDeliveryMode() {
        return this.deliveryMode;
    }

    public void setDeliveryMode(int deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getStopable() {
        return this.stopable;
    }

    public void setStopable(int stopable) {
        this.stopable = stopable;
    }

    public String getExceptionContent() {
        return this.exceptionContent;
    }

    public void setExceptionContent(String exceptionContent) {
        this.exceptionContent = exceptionContent;
    }

    public static byte[] toBytes(Header header) {
        int version = header.getVersion();
        int deliveryMode;
        int contentType;
        int contentEncoding;
        StringSerialize correlationID;
        StringSerialize messageID;
        StringSerialize serverName;
        int capacity ;
        switch(version) {
            case 1:
                capacity = 16;
                deliveryMode = header.getDeliveryMode();
                contentType = header.getContentType();
                contentEncoding = header.getContentEncoding();
                StringSerialize replyTo = new StringSerialize(header.getReplyTo());
                capacity = capacity + replyTo.getCapacity();
                correlationID = new StringSerialize(header.getCorrelationID());
                capacity += correlationID.getCapacity();
                messageID = new StringSerialize(header.getMessageID());
                capacity += messageID.getCapacity();
                serverName = new StringSerialize(header.getServerName());
                capacity += serverName.getCapacity();
                ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
                byteBuffer.putInt(version);
                byteBuffer.putInt(deliveryMode);
                byteBuffer.putInt(contentType);
                byteBuffer.putInt(contentEncoding);
                byteBuffer.putInt(replyTo.getLength());
                if (replyTo.getData() != null) {
                    byteBuffer.put(replyTo.getData());
                }

                byteBuffer.putInt(correlationID.getLength());
                if (correlationID.getData() != null) {
                    byteBuffer.put(correlationID.getData());
                }

                byteBuffer.putInt(messageID.getLength());
                if (messageID.getData() != null) {
                    byteBuffer.put(messageID.getData());
                }

                byteBuffer.putInt(serverName.getLength());
                if (serverName.getData() != null) {
                    byteBuffer.put(serverName.getData());
                }

                return byteBuffer.array();
            case 2:
                capacity = 20;
                deliveryMode = header.getDeliveryMode();
                contentType = header.getContentType();
                contentEncoding = header.getContentEncoding();
                int isStop = header.getStopable();
                correlationID = new StringSerialize(header.getReplyTo());
                capacity = capacity + correlationID.getCapacity();
                messageID = new StringSerialize(header.getCorrelationID());
                capacity += messageID.getCapacity();
                serverName = new StringSerialize(header.getMessageID());
                capacity += serverName.getCapacity();
                serverName = new StringSerialize(header.getServerName());
                capacity += serverName.getCapacity();
                StringSerialize exceptionContent = new StringSerialize(header.getExceptionContent());
                capacity += exceptionContent.getCapacity();
                byteBuffer = ByteBuffer.allocate(capacity);
                byteBuffer.putInt(version);
                byteBuffer.putInt(deliveryMode);
                byteBuffer.putInt(contentType);
                byteBuffer.putInt(contentEncoding);
                byteBuffer.putInt(isStop);
                byteBuffer.putInt(correlationID.getLength());
                if (correlationID.getData() != null) {
                    byteBuffer.put(correlationID.getData());
                }

                byteBuffer.putInt(messageID.getLength());
                if (messageID.getData() != null) {
                    byteBuffer.put(messageID.getData());
                }

                byteBuffer.putInt(serverName.getLength());
                if (serverName.getData() != null) {
                    byteBuffer.put(serverName.getData());
                }

                byteBuffer.putInt(serverName.getLength());
                if (serverName.getData() != null) {
                    byteBuffer.put(serverName.getData());
                }

                byteBuffer.putInt(exceptionContent.getLength());
                if (exceptionContent.getData() != null) {
                    byteBuffer.put(exceptionContent.getData());
                }

                return byteBuffer.array();
            default:
                return null;
        }
    }

    public static Header toHeader(byte[] data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        Header header = new Header();
        int version = byteBuffer.getInt();
        header.setVersion(version);
        switch(version) {
            case 1:
                header.setDeliveryMode(byteBuffer.getInt());
                header.setContentType(byteBuffer.getInt());
                header.setContentEncoding(byteBuffer.getInt());
                header.setReplyTo(getString(byteBuffer));
                header.setCorrelationID(getString(byteBuffer));
                header.setMessageID(getString(byteBuffer));
                header.setServerName(getString(byteBuffer));
                return header;
            case 2:
                header.setDeliveryMode(byteBuffer.getInt());
                header.setContentType(byteBuffer.getInt());
                header.setContentEncoding(byteBuffer.getInt());
                header.setStopable(byteBuffer.getInt());
                header.setReplyTo(getString(byteBuffer));
                header.setCorrelationID(getString(byteBuffer));
                header.setMessageID(getString(byteBuffer));
                header.setServerName(getString(byteBuffer));
                header.setExceptionContent(getString(byteBuffer));
                return header;
            default:
                return null;
        }
    }

    private static String getString(ByteBuffer byteBuffer) {
        int length = byteBuffer.getInt();
        if (length == -1) {
            return null;
        } else {
            byte[] data = new byte[length];
            byteBuffer.get(data);
            String content = new String(data, Charset.forName("UTF-8"));
            return content;
        }
    }

    public boolean isStop() {
        return this.stopable == 2;
    }

    public void setStop() {
        this.setStopable(2);
    }

    public String toString() {
        return "Header{version=" + this.version + ", deliveryMode=" + this.deliveryMode + ", contentType=" + this.contentType + ", contentEncoding=" + this.contentEncoding + ", stopable=" + this.stopable + ", replyTo='" + this.replyTo + '\'' + ", correlationID='" + this.correlationID + '\'' + ", messageID='" + this.messageID + '\'' + ", serverName='" + this.serverName + '\'' + ", exceptionContent='" + this.exceptionContent + '\'' + '}';
    }
}
