import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.exception.MQRedisException;
import java.util.List;

import com.creditease.ns.mq.redis.JedisClusterExtend;
import redis.clients.util.SafeEncoder;

public class ClusterRedis implements Redis {
    private JedisClusterExtend jc;
    private static final NsLog commandLog = NsLog.getMqLog("NS_MQ", "redis集群命令");

    public void lpush(String queueName, byte[] data) throws MQRedisException {
        try {
            this.jc.lpush(SafeEncoder.encode(queueName), new byte[][]{data});
        } catch (Exception var4) {
            commandLog.error("mq lpush error", var4);
            throw new MQRedisException(var4);
        }
    }

    public byte[] brpop(String queueName) throws MQRedisException {
        List result = null;

        try {
            result = this.jc.brpop(0, SafeEncoder.encode(queueName));
        } catch (Exception var4) {
            commandLog.error("mq lpush error", var4);
            throw new MQRedisException(var4);
        }

        return (byte[])result.get(1);
    }

    public byte[] brpop(String queueName, int timeout) throws MQRedisException {
        List<byte[]> result = this.jc.brpop(timeout, SafeEncoder.encode(queueName));
        return (byte[])result.get(1);
    }

    public void expired(String key, int expired) throws MQRedisException {
        this.jc.expire(SafeEncoder.encode(key), expired);
    }

    public byte[] rpop(String queueName) throws MQRedisException {
        return this.jc.rpop(SafeEncoder.encode(queueName));
    }

    public void lpushWithExpired(String queueName, byte[] data, int second) throws MQRedisException {
        String script = "local retCode = redis.call('LPUSH', KEYS[1], KEYS[2]);\n redis.call('EXPIRE', KEYS[1], KEYS[3]);\n return retCode";
        this.jc.eval(SafeEncoder.encode(script), SafeEncoder.encode(queueName), 3, new byte[][]{SafeEncoder.encode(queueName), data, SafeEncoder.encode(String.valueOf(second))});
    }

    public String ping() throws MQRedisException {
        String time = String.valueOf(System.currentTimeMillis());
        this.jc.lpush(time, new String[]{time});
        String checkTime = this.jc.rpop(time);
        return time.equals(checkTime) ? "pong" : "";
    }

    public ClusterRedis(JedisClusterExtend jc) {
        this.jc = jc;
    }
}
