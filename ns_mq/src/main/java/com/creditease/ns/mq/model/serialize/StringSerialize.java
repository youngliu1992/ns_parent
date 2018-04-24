package com.creditease.ns.mq.model.serialize;

import java.nio.charset.Charset;

public class StringSerialize {
    private String origString;
    private byte[] data;
    private int length;
    private int capacity;

    public StringSerialize(String origString) {
        this.origString = origString;
        if (origString == null) {
            this.length = -1;
            this.capacity = 4;
        } else {
            this.data = origString.getBytes(Charset.forName("UTF-8"));
            this.length = this.data.length;
            this.capacity = 4 + this.length;
        }

    }

    public String getOrigString() {
        return this.origString;
    }

    public void setOrigString(String origString) {
        this.origString = origString;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
