package com.creditease.ns.mq.redis;

import com.creditease.ns.mq.MQConfig;
import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.exception.MQInitError;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

public class SingleRedisBuilder {
    private int maxTotal = -1;
    private int miniIdle = -1;
    private int maxIdle = -1;
    private int connectionTimeout = -1;
    private int socketTimeout = -1;
    private String hostAndPort;

    protected SingleRedisBuilder() {
    }

    public static SingleRedisBuilder create() {
        return new SingleRedisBuilder();
    }

    public int getMaxTotal() {
        return this.maxTotal;
    }

    public SingleRedisBuilder setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
        return this;
    }

    public int getMiniIdle() {
        return this.miniIdle;
    }

    public SingleRedisBuilder setMiniIdle(int miniIdle) {
        this.miniIdle = miniIdle;
        return this;
    }

    public int getMaxIdle() {
        return this.maxIdle;
    }

    public SingleRedisBuilder setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public SingleRedisBuilder setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public int getSocketTimeout() {
        return this.socketTimeout;
    }

    public SingleRedisBuilder setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public String getHostAndPort() {
        return this.hostAndPort;
    }

    public SingleRedisBuilder setHostAndPort(String hostAndPort) {
        this.hostAndPort = hostAndPort;
        return this;
    }

    public RedisMQTemplate build() {
        if (this.maxIdle == -1) {
            this.maxTotal = MQConfig.getConfig.getRedisSingleMaxTotal();
            if (this.maxTotal == -1) {
                throw new IllegalArgumentException("SingleRedisBuilder was not set maxTotal value");
            }
        }

        if (this.connectionTimeout == -1) {
            this.connectionTimeout = MQConfig.getConfig.getRedisSingleConnectionTimeout();
        }

        if (this.socketTimeout == -1) {
            this.socketTimeout = MQConfig.getConfig.getRedisSingleSocketTimeout();
        }

        if (this.miniIdle == -1) {
            this.miniIdle = MQConfig.getConfig.getRedisSingleMinIdle();
        }

        if (this.maxIdle == -1) {
            this.maxIdle = MQConfig.getConfig.getRedisSingleMaxIdle();
        }

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(this.maxTotal);
        poolConfig.setMinIdle(this.miniIdle);
        poolConfig.setMaxIdle(this.maxIdle);
        if (this.hostAndPort == null) {
            this.hostAndPort = MQConfig.getConfig.getRedisSingleHost();
            if (this.hostAndPort == null) {
                throw new IllegalArgumentException("SingleRedisBuilder was not set host value");
            }
        }

        String[] hostPort = this.hostAndPort.split(":");
        JedisPool jp = null;
        if (hostPort.length == 2) {
            jp = new JedisPool(poolConfig, hostPort[0], Integer.parseInt(hostPort[1]), this.connectionTimeout, this.socketTimeout, (String)null, 0, (String)null);
        }

        RedisMQTemplate client = new RedisMQTemplate();
        client.setRedis(new SingleRedis(jp));

        try {
            boolean result = client.ping();
            if (result) {
                throw new MQInitError("redis服务不可用 hostAndPort:" + this.hostAndPort);
            } else {
                return client;
            }
        } catch (MQException var6) {
            var6.printStackTrace();
            throw new MQInitError("redis服务不可用 hostAndPort:" + this.hostAndPort);
        }
    }
}
