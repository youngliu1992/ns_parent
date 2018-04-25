package com.creditease.ns.transporter.handle;

import com.creditease.framework.exception.ThreadUncaughtExceptionHandler;
import com.creditease.framework.util.StringUtil;
import com.creditease.ns.framework.spring.SpringPlugin;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.transporter.buffer.BufferManager;
import com.creditease.ns.transporter.chain.service.AbstractServiceChainBridge;
import com.creditease.ns.transporter.config.ConfigManager;
import com.creditease.ns.transporter.config.InQueueInfo;
import com.creditease.ns.transporter.config.XmlConfigManager;
import com.creditease.ns.transporter.context.XmlAppTransporterContext;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultHandlerManager implements HandlerManager, LifeCycle {
    private boolean isStarted = false;
    private boolean isStartedHandle = false;
    private Map<String, InQueueInfo> queueNameToQueueInfos;
    private BufferManager bufferManager;
    private static HandlerManager self = new DefaultHandlerManager();
    private Map<String, ExecutorService> queueNameToExecutors = new LinkedHashMap();
    private ConfigManager configManager;
    private XmlAppTransporterContext context;
    private static NsLog frameLog;

    public DefaultHandlerManager() {
    }

    public void init() {
        self = this;
    }

    public synchronized void startUp() {
        if (!this.isStarted) {
            this.startHandle();
            this.isStarted = true;
        }

    }

    public void destroy() {
        long startTime = System.currentTimeMillis();
        frameLog.info("正在停止 {} {} cost:{}ms", new Object[]{this.queueNameToExecutors.size(), this.queueNameToQueueInfos.size(), System.currentTimeMillis() - startTime});
        Iterator it = this.queueNameToQueueInfos.keySet().iterator();

        while(it.hasNext()) {
            String queueName = (String)it.next();
            frameLog.info(" {} 正在停止 cost:{}ms", new Object[]{queueName, System.currentTimeMillis() - startTime});

            while(this.bufferManager.sizeOfReceiveBufferOf(queueName) > 0L) {
                ;
            }

            while(this.bufferManager.sizeOfSendBufferOf(queueName) > 0L) {
                ;
            }

            ((ExecutorService)this.queueNameToExecutors.get(queueName)).shutdownNow();
            this.queueNameToExecutors.remove(queueName);
            frameLog.info(" {} 停止 成功 cost:{}ms", new Object[]{queueName, System.currentTimeMillis() - startTime});
        }

        frameLog.info(" 停止 成功 {} {} cost:{}ms", new Object[]{this.queueNameToExecutors.size(), this.queueNameToQueueInfos.size(), System.currentTimeMillis() - startTime});
    }

    public synchronized void startHandle() {
        if (!this.isStartedHandle) {
            Iterator it = this.queueNameToQueueInfos.keySet().iterator();

            while(it.hasNext()) {
                String queueName = (String)it.next();
                InQueueInfo inQueueInfo = (InQueueInfo)this.queueNameToQueueInfos.get(queueName);
                int handlerMaxNum = inQueueInfo.getHandlerNum();
                ExecutorService scheduler = Executors.newFixedThreadPool(handlerMaxNum, new DefaultHandlerManager.CustomThreadNameThreadFactory("h", queueName));
                Object handlerTarget = null;
                if (this.configManager instanceof XmlConfigManager) {
                    XmlConfigManager xmlConfigManager = (XmlConfigManager)this.configManager;
                    String serviceClassName = inQueueInfo.getServiceClassName();

                    try {
                        Class cl = Class.forName(serviceClassName);

                        try {
                            Object o = null;
                            if (xmlConfigManager.isSpring()) {
                                SpringPlugin springPlugin = inQueueInfo.getSpringPlugin();
                                o = springPlugin.getBeanByClassName(cl);
                            } else {
                                o = cl.newInstance();
                            }

                            handlerTarget = o;
                            if (inQueueInfo.getRefCatalogId() != null) {
                                ((AbstractServiceChainBridge)o).setCatalogId(inQueueInfo.getRefCatalogId());
                            }
                        } catch (InstantiationException var12) {
                            var12.printStackTrace();
                            throw new RuntimeException("实例化错误" + serviceClassName);
                        } catch (IllegalAccessException var13) {
                            var13.printStackTrace();
                            throw new RuntimeException("实例化错误,不允许访问构造方法" + serviceClassName);
                        }
                    } catch (ClassNotFoundException var14) {
                        var14.printStackTrace();
                        throw new RuntimeException("没有找到" + serviceClassName);
                    } catch (Exception var15) {
                        throw new RuntimeException(StringUtil.getStackTrace(var15));
                    }
                }

                for(int i = 0; i < handlerMaxNum; ++i) {
                    Handler handler = new DefaultHandler();
                    DefaultHandler defaultHandler = (DefaultHandler)handler;
                    defaultHandler.setQueueName(queueName);
                    defaultHandler.setBufferManager(this.bufferManager);
                    defaultHandler.setContext(this.context);
                    defaultHandler.setServiceInstance(handlerTarget);
                    scheduler.execute(defaultHandler);
                }
            }

            this.isStartedHandle = true;
        }

    }

    public Map<String, InQueueInfo> getQueueNameToQueueInfos() {
        return this.queueNameToQueueInfos;
    }

    public void setQueueNameToQueueInfos(Map<String, InQueueInfo> queueNameToQueueInfos) {
        this.queueNameToQueueInfos = Collections.unmodifiableMap(queueNameToQueueInfos);
    }

    public BufferManager getBufferManager() {
        return this.bufferManager;
    }

    public void setBufferManager(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public static synchronized HandlerManager getInstance() {
        if (self == null) {
            self = new DefaultHandlerManager();
        }

        return self;
    }

    public XmlAppTransporterContext getContext() {
        return this.context;
    }

    public void setContext(XmlAppTransporterContext context) {
        this.context = context;
    }

    static {
        frameLog = XmlAppTransporterContext.frameLog;
    }

    static class CustomThreadNameThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        CustomThreadNameThreadFactory(String threadNamePrefix, String queueName) {
            SecurityManager s = System.getSecurityManager();
            this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = threadNamePrefix + "-" + queueName + "-" + poolNumber.getAndIncrement() + "-t-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
            t.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }

            if (t.getPriority() != 5) {
                t.setPriority(5);
            }

            return t;
        }
    }
}
