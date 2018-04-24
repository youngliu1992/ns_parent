package com.creditease.ns.mq;

import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.utils.PropertiesHelper;
import java.util.Properties;

public class MQConfig {
    private static final NsLog initLog = NsLog.getMqLog("NS_MQ", "初始化");
    public static MQConfig getConfig = new MQConfig();
    private final String DEFAULT_MAXTOTAL = "50";
    private final String DEFAULT_MINIDLE = "1";
    private final String DEFAULT_MAXIDLE = "10";
    private final String DEFAULT_CONNECTIONTIMEOUT = "5000";
    private final String DEFAULT_SOCKETTIMEOUT = "5000";
    private final String DEFAULT_EXPIRED = "5000";
    private final String DEFAULT_QUEUE_PREFIX = "";
    private final String DEFAULT_MAXREDIRECTIONS = "5";
    private final String REDIS_DEFUALT_TYPE = "1";
    private int redisDefaultType;
    private String redisClusterHost;
    private int redisClusterMaxTotal;
    private int redisClusterMiniIdle;
    private int redisClusterMaxIdle;
    private int redisClusterConnectionTimeout;
    private int redisClusterMaxRedirections;
    private String redisSingleHost;
    private int redisSingleMaxTotal;
    private int redisSingleMinIdle;
    private int redisSingleMaxIdle;
    private int redisSingleConnectionTimeout;
    private int redisSingleSocketTimeout;
    private int tempQueueExpired;
    private String queuePrefix;

    public MQConfig() {
        Properties properties = PropertiesHelper.getMQProperteis("ns_mq.properties");
        this.redisDefaultType = Integer.parseInt(properties.getProperty("redis.type", "1"));
        this.redisSingleHost = properties.getProperty("redis.single.host");
        this.redisSingleMaxTotal = Integer.parseInt(properties.getProperty("redis.single.maxTotal", "50"));
        this.redisSingleMinIdle = Integer.parseInt(properties.getProperty("redis.single.miniIdle", "1"));
        this.redisSingleMaxIdle = Integer.parseInt(properties.getProperty("redis.single.maxIdle", "10"));
        this.redisSingleConnectionTimeout = Integer.parseInt(properties.getProperty("redis.single.connectionTimeout", "5000"));
        this.redisSingleSocketTimeout = Integer.parseInt(properties.getProperty("redis.single.socketTimeout", "5000"));
        this.redisClusterHost = properties.getProperty("redis.cluster.host");
        this.redisClusterMaxTotal = Integer.parseInt(properties.getProperty("redis.cluster.maxTotal", "50"));
        this.redisClusterMiniIdle = Integer.parseInt(properties.getProperty("redis.cluster.miniIdle", "1"));
        this.redisClusterMaxIdle = Integer.parseInt(properties.getProperty("redis.cluster.maxIdle", "10"));
        this.redisClusterConnectionTimeout = Integer.parseInt(properties.getProperty("redis.cluster.connectionTimeout", "5000"));
        this.redisClusterMaxRedirections = Integer.parseInt(properties.getProperty("redis.cluster.maxRedirections", "5"));
        this.tempQueueExpired = Integer.parseInt(properties.getProperty("redis.temp.queue.expired", "5000"));
        this.queuePrefix = properties.getProperty("redis.queue.prefix", "");
        if (this.redisDefaultType == 1) {
            initLog.info("redis服务器类型为:{}", new Object[]{"单机"});
            initLog.info("redis单机服务器地址:{}", new Object[]{this.redisSingleHost});
            initLog.info("redis单机服务器最大连接数:{}", new Object[]{this.redisSingleMaxTotal});
            initLog.info("redis单机服务器最小空闲数:{}", new Object[]{this.redisSingleMinIdle});
            initLog.info("redis单机服务器最大空闲数:{}", new Object[]{this.redisSingleMaxIdle});
            initLog.info("redis单机连接超时时间:{}ms", new Object[]{this.redisSingleConnectionTimeout});
            initLog.info("redis单机socket超时时间:{}ms", new Object[]{this.redisSingleSocketTimeout});
        } else {
            initLog.info("redis服务器类型为:{}", new Object[]{"集群"});
            initLog.info("redis集群服务器地址:{}", new Object[]{this.redisClusterHost});
            initLog.info("redis集群最大连接数:{}", new Object[]{this.redisClusterMaxTotal});
            initLog.info("redis集群最小空闲数:{}", new Object[]{this.redisClusterMiniIdle});
            initLog.info("redis集群最大空闲数:{}", new Object[]{this.redisClusterMaxIdle});
            initLog.info("redis集群连接时间和socket超时时间:{}", new Object[]{this.redisClusterConnectionTimeout});
            initLog.info("redis集群最大跳转次数:{}", new Object[]{this.redisClusterMaxRedirections});
        }

        initLog.info("redis临时队列过期时间:{}ms", new Object[]{this.tempQueueExpired});
        initLog.info("redis队列名前缀:{}", new Object[]{this.queuePrefix});
    }

    public String getRedisClusterHost() {
        return this.redisClusterHost;
    }

    public int getRedisClusterMaxTotal() {
        return this.redisClusterMaxTotal;
    }

    public int getRedisClusterMiniIdle() {
        return this.redisClusterMiniIdle;
    }

    public int getRedisClusterMaxIdle() {
        return this.redisClusterMaxIdle;
    }

    public int getRedisClusterMaxRedirections() {
        return this.redisClusterMaxRedirections;
    }

    public int getRedisClusterConnectionTimeout() {
        return this.redisClusterConnectionTimeout;
    }

    public String getRedisSingleHost() {
        return this.redisSingleHost;
    }

    public int getRedisSingleMaxTotal() {
        return this.redisSingleMaxTotal;
    }

    public int getRedisSingleConnectionTimeout() {
        return this.redisSingleConnectionTimeout;
    }

    public int getRedisSingleSocketTimeout() {
        return this.redisSingleSocketTimeout;
    }

    public int getRedisSingleMinIdle() {
        return this.redisSingleMinIdle;
    }

    public int getRedisSingleMaxIdle() {
        return this.redisSingleMaxIdle;
    }

    public int getTempQueueExpired() {
        return this.tempQueueExpired;
    }

    public String getQueuePrefix() {
        return this.queuePrefix;
    }

    public void setQueuePrefix(String queuePrefix) {
        this.queuePrefix = queuePrefix;
    }

    public int getRedisDefaultType() {
        return this.redisDefaultType;
    }
}
