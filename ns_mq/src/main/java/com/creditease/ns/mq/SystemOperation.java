package com.creditease.ns.mq;

import com.creditease.ns.mq.exception.MQException;

public interface SystemOperation {
    boolean ping() throws MQException;
}