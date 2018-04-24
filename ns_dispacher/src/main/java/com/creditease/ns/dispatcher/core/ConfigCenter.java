package com.creditease.ns.dispatcher.core;

import com.creditease.framework.util.PropertiesUtil;
import com.creditease.ns.log.NsLog;

public class ConfigCenter
{
    private static NsLog initLog = NsLog.getFramLog("Dispatcher", "分发器");
    public static ConfigCenter getConfig = new ConfigCenter();

    private final int defaultHttpPort = 8027;
    private int httpPort;
    private final int DEFAULT_DISPATCHER_POOLNUM = 5;
    private int dispatcherPoolNum;
    private final String DEFAULT_QUEUENAME = "controller";
    private String queueName;

    private ConfigCenter()
    {
        try
        {
            PropertiesUtil pu = new PropertiesUtil("ns_dispatcher.properties");
            int _httpPort = pu.getInt("http.port", 8027);
            setHttpPort(_httpPort);
            initLog.info("读取配置:HTTP端口号:{}", new Object[] { Integer.valueOf(_httpPort) });

            int _dispatcherPoolNum = pu.getInt("dispatcher.pool.num", 5);
            setDispatcherPoolNum(_dispatcherPoolNum);
            initLog.info("读取配置:启动线程数:{}", new Object[] { Integer.valueOf(_dispatcherPoolNum) });

            String _queueName = pu.getString("dispatcher.queuename", "controller");
            setQueueName(_queueName);
            initLog.info("读取配置:发送Controller队列名称:{}", new Object[] { _queueName });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getHttpPort() {
        return this.httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getDispatcherPoolNum() {
        return this.dispatcherPoolNum;
    }

    public void setDispatcherPoolNum(int dispatcherPoolNum) {
        this.dispatcherPoolNum = dispatcherPoolNum;
    }

    public String getQueueName() {
        return this.queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}