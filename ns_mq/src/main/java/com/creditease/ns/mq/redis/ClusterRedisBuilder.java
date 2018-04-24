package com.creditease.ns.mq.redis;

import com.creditease.ns.mq.MQConfig;
import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.exception.MQInitError;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;

public class ClusterRedisBuilder {
    private int maxTotal = -1;
    private int miniIdle = -1;
    private int maxIdle = -1;
    private int connectionTimeout = -1;
    private int socketTimeout = -1;
    private int maxRedirections = -1;
    private String hostAndPorts;

    private ClusterRedisBuilder() {
    }

    public static ClusterRedisBuilder create() {
        return new ClusterRedisBuilder();
    }

    public ClusterRedisBuilder setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
        return this;
    }

    public ClusterRedisBuilder setMiniIdle(int miniIdle) {
        this.miniIdle = miniIdle;
        return this;
    }

    public ClusterRedisBuilder setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }

    public ClusterRedisBuilder setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public ClusterRedisBuilder setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public ClusterRedisBuilder setHostAndPorts(String hostAndPorts) {
        this.hostAndPorts = hostAndPorts;
        return this;
    }

    public RedisMQTemplate build() {
        if (this.maxTotal == -1) {
            this.maxTotal = MQConfig.getConfig.getRedisClusterMaxTotal();
            if (this.maxTotal == -1) {
                throw new IllegalArgumentException("ClusterRedisBuilder was not set maxTotal value");
            }
        }

        if (this.miniIdle == -1) {
            this.miniIdle = MQConfig.getConfig.getRedisClusterMiniIdle();
        }

        if (this.maxIdle == -1) {
            this.maxIdle = MQConfig.getConfig.getRedisClusterMaxIdle();
        }

        if (this.connectionTimeout == -1) {
            this.connectionTimeout = MQConfig.getConfig.getRedisClusterConnectionTimeout();
        }

        if (this.maxRedirections == -1) {
            this.maxRedirections = MQConfig.getConfig.getRedisClusterMaxRedirections();
        }

        if (this.hostAndPorts == null) {
            this.hostAndPorts = MQConfig.getConfig.getRedisClusterHost();
            if (this.hostAndPorts == null) {
                throw new IllegalArgumentException("ClusterRedisBuilder was not set hostAndPorts value");
            }
        }

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(this.maxTotal);
        poolConfig.setMinIdle(this.miniIdle);
        poolConfig.setMinIdle(this.maxIdle);
        String[] hostArray = this.hostAndPorts.split(";");
        Set<HostAndPort> jedisClusterNodes = new HashSet();
        String[] arr$ = hostArray;
        int len$ = hostArray.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String hostItem = arr$[i$];
            String[] hostPort = hostItem.split(":");
            if (hostPort.length == 2) {
                jedisClusterNodes.add(new HostAndPort(hostPort[0], Integer.parseInt(hostPort[1])));
            }
        }

        JedisClusterExtend jc = new JedisClusterExtend(jedisClusterNodes, this.connectionTimeout, this.maxRedirections, poolConfig);
        ClusterRedis clusterRedis = new ClusterRedis(jc);
        RedisMQTemplate client = new RedisMQTemplate();
        client.setRedis(clusterRedis);

        try {
            boolean result = client.ping();
            if (result) {
                throw new MQInitError("redis服务不可用 hostAndPorts:" + this.hostAndPorts);
            } else {
                return client;
            }
        } catch (MQException var9) {
            var9.printStackTrace();
            throw new MQInitError("redis服务不可用 hostAndPorts:" + this.hostAndPorts);
        }
    }
}
