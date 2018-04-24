package com.creditease.ns.mq.redis;

import com.creditease.ns.log.NsLog;
import com.creditease.ns.mq.MQConfig;
import com.creditease.ns.mq.MQTemplate;
import com.creditease.ns.mq.exception.MQArgumentException;
import com.creditease.ns.mq.exception.MQException;
import com.creditease.ns.mq.exception.MQRedisException;
import com.creditease.ns.mq.exception.MQTimeOutException;
import com.creditease.ns.mq.model.DeliveryMode;
import com.creditease.ns.mq.model.Message;

public class RedisMQTemplate extends MQTemplate {
    private static final NsLog commandLog = NsLog.getMqLog("NS_MQ", "redis集群命令");
    Redis redis;

    public RedisMQTemplate() {
    }

    public void send(String queueName, byte[] body) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName != null && queueName.length() != 0) {
            if (body != null && body.length != 0) {
                Message message = new Message(DeliveryMode.ASYNC);
                message.setBody(body);

                try {
                    queueName = this.getActiveQueueName(queueName);
                    this.redis.lpush(queueName, message.toBytes());
                    commandLog.debug("send message msgId:{} to queueName:{}，use:[{}ms", new Object[]{message.getHeader().getMessageID(), queueName, System.currentTimeMillis() - start});
                } catch (MQRedisException var7) {
                    throw var7;
                } catch (Exception var8) {
                    throw new MQException(var8);
                }
            } else {
                throw new MQArgumentException("The byte[] body is illegal");
            }
        } else {
            throw new MQArgumentException("queueName is illegal");
        }
    }

    public void send(String queueName, String msgId, byte[] body) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName != null && queueName.length() != 0) {
            if (body != null && body.length != 0) {
                Message message = new Message(msgId, DeliveryMode.ASYNC);
                message.setBody(body);

                try {
                    queueName = this.getActiveQueueName(queueName);
                    this.redis.lpush(queueName, message.toBytes());
                    commandLog.debug("send message msgId:{} to queueName:{}，use:[{}ms", new Object[]{message.getHeader().getMessageID(), queueName, System.currentTimeMillis() - start});
                } catch (MQRedisException var8) {
                    throw var8;
                } catch (Exception var9) {
                    throw new MQException(var9);
                }
            } else {
                throw new MQArgumentException("The byte[] body is illegal");
            }
        } else {
            throw new MQArgumentException("queueName is illegal");
        }
    }

    public void send(String queueName, Message message) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName != null && queueName.length() != 0) {
            try {
                queueName = this.getActiveQueueName(queueName);
                this.redis.lpush(queueName, message.toBytes());
                commandLog.debug("send message msgId:{} to queueName:{}，use:[{}ms", new Object[]{message.getHeader().getMessageID(), queueName, System.currentTimeMillis() - start});
            } catch (MQRedisException var6) {
                throw var6;
            } catch (Exception var7) {
                throw new MQException(var7);
            }
        } else {
            throw new MQArgumentException("queueName is illegal");
        }
    }

    public Message receive(String queueName) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName != null && queueName.length() != 0) {
            byte[] result;
            try {
                queueName = this.getActiveQueueName(queueName);
                result = this.redis.brpop(queueName);
            } catch (MQRedisException var6) {
                throw var6;
            } catch (Exception var7) {
                throw new MQException(var7);
            }

            if (result == null) {
                return null;
            } else {
                Message message = new Message(result);
                commandLog.debug("receive message msgId:{} from queueName:{}，use:[{}ms]", new Object[]{message.getHeader().getMessageID(), queueName, System.currentTimeMillis() - start});
                return message;
            }
        } else {
            throw new MQArgumentException("queueName is illegal");
        }
    }

    public Message receive(String queueName, int timeout) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName != null && queueName.length() != 0) {
            byte[] result;
            try {
                queueName = this.getActiveQueueName(queueName);
                result = this.redis.brpop(queueName, timeout);
            } catch (MQRedisException var7) {
                throw var7;
            } catch (Exception var8) {
                throw new MQException(var8);
            }

            if (result == null) {
                return null;
            } else {
                Message message = new Message(result);
                commandLog.debug("receive message msgId:{} from queueName:{}，use:[{}ms]", new Object[]{message.getHeader().getMessageID(), queueName, System.currentTimeMillis() - start});
                return message;
            }
        } else {
            throw new MQArgumentException("queueName is illegal");
        }
    }

    public Message receiveNoneBlock(String queueName) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName != null && queueName.length() != 0) {
            byte[] result;
            try {
                queueName = this.getActiveQueueName(queueName);
                result = this.redis.rpop(queueName);
            } catch (MQRedisException var6) {
                throw var6;
            } catch (Exception var7) {
                throw new MQException(var7);
            }

            if (result == null) {
                return null;
            } else {
                Message message = new Message(result);
                commandLog.debug("receive message msgId:{} from queueName:{}，use:[{}ms]", new Object[]{message.getHeader().getMessageID(), queueName, System.currentTimeMillis() - start});
                return message;
            }
        } else {
            throw new MQArgumentException("queueName is illegal");
        }
    }

    public Message publish(String queueName, byte[] body) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName != null && queueName.length() != 0) {
            if (body != null && body.length != 0) {
                Message message = new Message(DeliveryMode.SYNC);
                message.setBody(body);

                try {
                    queueName = this.getActiveQueueName(queueName);
                    this.redis.lpush(queueName, message.toBytes());
                    commandLog.debug("publish message msgId:{} to queueName:{}，use:[{}ms]", new Object[]{message.getHeader().getMessageID(), queueName, System.currentTimeMillis() - start});
                } catch (MQRedisException var13) {
                    throw var13;
                } catch (Exception var14) {
                    throw new MQException(var14);
                }

                long replyStart = System.currentTimeMillis();
                String replyQueue = message.getHeader().getReplyTo();

                byte[] response;
                try {
                    response = this.redis.brpop(replyQueue);
                } catch (MQRedisException var11) {
                    throw var11;
                } catch (Exception var12) {
                    throw new MQException(var12);
                }

                Message resultMessage = new Message(response);
                commandLog.debug("get reply message msgId:{},use:[{}ms]", new Object[]{resultMessage.getHeader().getMessageID(), System.currentTimeMillis() - replyStart});
                return resultMessage;
            } else {
                throw new MQArgumentException("The byte[] body is illegal");
            }
        } else {
            throw new MQArgumentException("queueName is illegal");
        }
    }

    public Message publish(String queueName, byte[] body, int timeout) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName != null && queueName.length() != 0) {
            if (body != null && body.length != 0) {
                if (timeout <= 0) {
                    throw new MQArgumentException("The int timeout is illegal");
                } else {
                    Message message = new Message(DeliveryMode.SYNC);
                    message.setBody(body);

                    try {
                        queueName = this.getActiveQueueName(queueName);
                        this.redis.lpush(queueName, message.toBytes());
                        commandLog.debug("publish message msgId:{} to queueName:{}，,use:[{}ms]", new Object[]{message.getHeader().getMessageID(), queueName, System.currentTimeMillis() - start});
                    } catch (MQRedisException var14) {
                        throw var14;
                    } catch (Exception var15) {
                        throw new MQException(var15);
                    }

                    long replyStart = System.currentTimeMillis();
                    String replyQueue = message.getHeader().getReplyTo();

                    byte[] response;
                    try {
                        response = this.redis.brpop(replyQueue, timeout);
                    } catch (MQRedisException var12) {
                        throw var12;
                    } catch (Exception var13) {
                        throw new MQException(var13);
                    }

                    if (response == null) {
                        throw new MQTimeOutException();
                    } else {
                        Message retMessage = new Message(response);
                        commandLog.debug("get reply message msgId:{},use:[{}ms]", new Object[]{retMessage.getHeader().getMessageID(), System.currentTimeMillis() - replyStart});
                        return retMessage;
                    }
                }
            } else {
                throw new MQArgumentException("The byte[] body is illegal");
            }
        } else {
            throw new MQArgumentException("queueName is illegal");
        }
    }

    public Message publish(String queueName, byte[] body, String msgId, int timeout) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName != null && queueName.length() != 0) {
            if (body != null && body.length != 0) {
                if (timeout <= 0) {
                    throw new MQArgumentException("The int timeout is illegal");
                } else {
                    Message message = new Message(msgId, DeliveryMode.SYNC);
                    message.setBody(body);

                    try {
                        queueName = this.getActiveQueueName(queueName);
                        this.redis.lpush(queueName, message.toBytes());
                        commandLog.debug("publish message msgId:{} to queueName:{}，,use:[{}ms]", new Object[]{message.getHeader().getMessageID(), queueName, System.currentTimeMillis() - start});
                    } catch (MQRedisException var15) {
                        throw var15;
                    } catch (Exception var16) {
                        throw new MQException(var16);
                    }

                    long replyStart = System.currentTimeMillis();
                    String replyQueue = message.getHeader().getReplyTo();

                    byte[] response;
                    try {
                        response = this.redis.brpop(replyQueue, timeout);
                    } catch (MQRedisException var13) {
                        throw var13;
                    } catch (Exception var14) {
                        throw new MQException(var14);
                    }

                    if (response == null) {
                        throw new MQTimeOutException();
                    } else {
                        Message retMessage = new Message(response);
                        commandLog.debug("get reply message msgId:{},use:[{}ms]", new Object[]{retMessage.getHeader().getMessageID(), System.currentTimeMillis() - replyStart});
                        return retMessage;
                    }
                }
            } else {
                throw new MQArgumentException("The byte[] body is illegal");
            }
        } else {
            throw new MQArgumentException("queueName is illegal");
        }
    }

    public Message publish(String queueName, Message message) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName != null && queueName.length() != 0) {
            try {
                queueName = this.getActiveQueueName(queueName);
                this.redis.lpush(queueName, message.toBytes());
                commandLog.debug("publish message msgId:{} to  queueName:{}，use:[{}ms]", new Object[]{message.getHeader().getMessageID(), queueName, System.currentTimeMillis() - start});
            } catch (MQRedisException var12) {
                throw var12;
            } catch (Exception var13) {
                throw new MQException(var13);
            }

            long replyStart = System.currentTimeMillis();
            String replyQueue = message.getHeader().getReplyTo();

            byte[] response;
            try {
                response = this.redis.brpop(replyQueue);
            } catch (MQRedisException var10) {
                throw var10;
            } catch (Exception var11) {
                throw new MQException(var11);
            }

            Message retMessage = new Message(response);
            commandLog.debug("get reply message msgId:{},use:[{}ms]", new Object[]{retMessage.getHeader().getMessageID(), System.currentTimeMillis() - replyStart});
            return retMessage;
        } else {
            throw new MQArgumentException("queueName is illegal");
        }
    }

    public Message publish(String queueName, Message message, int timeout) throws MQException {
        long start = System.currentTimeMillis();
        if (queueName != null && queueName.length() != 0) {
            if (message == null) {
                throw new MQArgumentException("The byte[] body is illegal");
            } else if (timeout <= 0) {
                throw new MQArgumentException("The int timeout is illegal");
            } else {
                try {
                    queueName = this.getActiveQueueName(queueName);
                    this.redis.lpush(queueName, message.toBytes());
                    commandLog.debug("publish message msgId:{} to queueName:{}，use:[{}ms]", new Object[]{message.getHeader().getMessageID(), queueName, System.currentTimeMillis() - start});
                } catch (MQRedisException var13) {
                    throw var13;
                } catch (Exception var14) {
                    throw new MQException(var14);
                }

                long replyStart = System.currentTimeMillis();
                String replyQueue = message.getHeader().getReplyTo();

                byte[] response;
                try {
                    response = this.redis.brpop(replyQueue, timeout);
                } catch (MQRedisException var11) {
                    throw var11;
                } catch (Exception var12) {
                    throw new MQException(var12);
                }

                if (response == null) {
                    throw new MQTimeOutException();
                } else {
                    Message retMessage = new Message(response);
                    commandLog.debug("get reply message msgId:{},use:[{}ms]", new Object[]{retMessage.getHeader().getMessageID(), System.currentTimeMillis() - replyStart});
                    return retMessage;
                }
            }
        } else {
            throw new MQArgumentException("queueName is illegal");
        }
    }

    public void reply(Message message) throws MQException {
        long start = System.currentTimeMillis();
        String replyQueue = message.getHeader().getReplyTo();

        try {
            this.redis.lpushWithExpired(replyQueue, message.toBytes(), MQConfig.getConfig.getTempQueueExpired());
            commandLog.debug("reply message msgId:{} to queueName:{}，use:[{}ms]", new Object[]{message.getHeader().getMessageID(), replyQueue, System.currentTimeMillis() - start});
        } catch (MQRedisException var6) {
            throw var6;
        } catch (Exception var7) {
            throw new MQException(var7);
        }
    }

    public boolean ping() throws MQException {
        try {
            String check = this.redis.ping();
            return "pong".equals(check);
        } catch (Exception var2) {
            throw new MQRedisException(var2);
        }
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }
}
