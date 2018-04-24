package com.creditease.ns.mq.redis;

import com.creditease.ns.mq.exception.MQRedisException;

public interface Redis {
    void lpush(String var1, byte[] var2) throws MQRedisException;

    void lpushWithExpired(String var1, byte[] var2, int var3) throws MQRedisException;

    byte[] brpop(String var1) throws MQRedisException;

    byte[] brpop(String var1, int var2) throws MQRedisException;

    byte[] rpop(String var1) throws MQRedisException;

    void expired(String var1, int var2) throws MQRedisException;

    String ping() throws MQRedisException;
}