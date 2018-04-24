package com.creditease.ns.mq.redis;

import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.exception.MQRedisException;
import java.util.List;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;

public class SingleRedis implements Redis {
    private static final NsLog commandLog = NsLog.getMqLog("NS_MQ", "redis单机命令");
    private JedisPool jedisPool;

    public SingleRedis(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void lpush(String queueName, byte[] data) throws MQRedisException {
        Jedis jedis = this.jedisPool.getResource();

        try {
            jedis.lpush(SafeEncoder.encode(queueName), new byte[][]{data});
        } catch (Exception var8) {
            commandLog.error("mq lpush error", var8);
            throw new MQRedisException(var8);
        } finally {
            if (jedis != null) {
                jedis.close();
            }

        }

    }

    public byte[] brpop(String queueName) throws MQRedisException {
        Jedis jedis = this.jedisPool.getResource();
        List result = null;

        try {
            result = jedis.brpop(0, new byte[][]{SafeEncoder.encode(queueName)});
        } catch (Exception var8) {
            commandLog.error("mq brpop error", var8);
            throw new MQRedisException(var8);
        } finally {
            if (jedis != null) {
                jedis.close();
            }

        }

        return result != null ? (byte[])result.get(1) : null;
    }

    public byte[] brpop(String queueName, int timeout) throws MQRedisException {
        Jedis jedis = this.jedisPool.getResource();
        List result = null;

        try {
            result = jedis.brpop(timeout, new byte[][]{SafeEncoder.encode(queueName)});
        } catch (Exception var9) {
            commandLog.error("mq brpop error", var9);
            throw new MQRedisException(var9);
        } finally {
            if (jedis != null) {
                jedis.close();
            }

        }

        return result != null ? (byte[])result.get(1) : null;
    }

    public void expired(String key, int expired) throws MQRedisException {
        Jedis jedis = this.jedisPool.getResource();

        try {
            jedis.expire(key, expired);
        } catch (Exception var8) {
            commandLog.error("mq expired error", var8);
            throw new MQRedisException(var8);
        } finally {
            if (jedis != null) {
                jedis.close();
            }

        }

    }

    public byte[] rpop(String queueName) throws MQRedisException {
        Jedis jedis = this.jedisPool.getResource();
        Object var3 = null;

        byte[] result;
        try {
            result = jedis.rpop(SafeEncoder.encode(queueName));
        } catch (Exception var8) {
            commandLog.error("mq rpop error", var8);
            throw new MQRedisException(var8);
        } finally {
            if (jedis != null) {
                jedis.close();
            }

        }

        return result;
    }

    public void lpushWithExpired(String queueName, byte[] data, int second) throws MQRedisException {
        Jedis jedis = this.jedisPool.getResource();

        try {
            String sha1 = jedis.scriptLoad("local retCode = redis.call('LPUSH', KEYS[1], KEYS[2]);\n redis.call('EXPIRE', KEYS[1], KEYS[3]);\n return retCode");
            jedis.evalsha(SafeEncoder.encode(sha1), 3, new byte[][]{SafeEncoder.encode(queueName), data, SafeEncoder.encode(String.valueOf(second))});
        } catch (Exception var9) {
            commandLog.error("mq lpushWithExpired error", var9);
            throw new MQRedisException(var9);
        } finally {
            if (jedis != null) {
                jedis.close();
            }

        }

    }

    public String ping() throws MQRedisException {
        Jedis jedis = this.jedisPool.getResource();

        String var2;
        try {
            var2 = jedis.ping();
        } catch (Exception var6) {
            commandLog.error("mq ping error", var6);
            throw new MQRedisException(var6);
        } finally {
            if (jedis != null) {
                jedis.close();
            }

        }

        return var2;
    }
}
