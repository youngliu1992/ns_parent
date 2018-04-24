package com.creditease.ns.mq;

import com.creditease.ns.mq.redis.ClusterRedisBuilder;
import com.creditease.ns.mq.redis.SingleRedisBuilder;

public class MQTemplates {
    public MQTemplates() {
    }

    public static MQTemplate defaultTemplate() {
        return MQConfig.getConfig.getRedisDefaultType() == 2 ? clusterRedisMQTemplate() : singleRedisMQTemplate();
    }

    public static MQTemplate clusterRedisMQTemplate() {
        return clusterRedisCustom().build();
    }

    public static ClusterRedisBuilder clusterRedisCustom() {
        return ClusterRedisBuilder.create();
    }

    public static MQTemplate singleRedisMQTemplate() {
        return singleRedisCustom().build();
    }

    public static SingleRedisBuilder singleRedisCustom() {
        return SingleRedisBuilder.create();
    }
}
