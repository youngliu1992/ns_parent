package com.creditease.ns.transporter.context;

import com.creditease.framework.listener.ExceptionListener;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.framework.startup.LifeCycleManager;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.log.util.PrintUtil;
import com.creditease.ns.transporter.buffer.BufferManager;
import com.creditease.ns.transporter.buffer.DefaultBufferManager;
import com.creditease.ns.transporter.config.InQueueInfo;
import com.creditease.ns.transporter.config.XmlConfigManager;
import com.creditease.ns.transporter.fetch.DefaultFetcherManager;
import com.creditease.ns.transporter.handle.DefaultHandlerManager;
import com.creditease.ns.transporter.send.DefaultSenderManager;
import com.creditease.ns.transporter.send.SenderManager;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

public class XmlAppTransporterContext implements LifeCycleManager, TransporterContext, Runnable {
    public static NsLog frameLog = NsLog.getFramLog("TransportStart", "TransportStart");
    private boolean isStarted;
    private String resoucePath;
    private static XmlAppTransporterContext self;
    private boolean isRunning = false;
    public static final String CONFIG_KEY = "configfile";
    private Map<String, ExceptionListener> exceptionListeners = new LinkedHashMap();
    private Stack<LifeCycle> lifeCycles = new Stack();

    public XmlAppTransporterContext() {
    }

    public void startUp() {
        if (!this.isStarted) {
            long startTime = System.currentTimeMillis();
            frameLog.info("# 启动框架各组件(5) #", new Object[0]);
            XmlConfigManager configManager = (XmlConfigManager)XmlConfigManager.getInstance();
            configManager.setResourcePath(this.resoucePath);
            configManager.setXmlAppTransporterContext(this);
            configManager.startUp();
            this.registerLifeCycle(configManager);
            frameLog.info("# (1/5)启动ConfigManager OK #", new Object[0]);
            Map<String, InQueueInfo> inQueueInfos = configManager.getQueueNameToQueueInfos();
            BufferManager bufferManager = DefaultBufferManager.getInstance();
            DefaultBufferManager defaultBufferManager = (DefaultBufferManager)bufferManager;
            defaultBufferManager.setQueueNameToInQueueInfo(inQueueInfos);
            defaultBufferManager.startUp();
            this.registerLifeCycle(defaultBufferManager);
            frameLog.info("# (2/5)启动BufferManager OK #", new Object[0]);
            SenderManager senderManager = DefaultSenderManager.getInstance();
            DefaultSenderManager defaultSenderManager = (DefaultSenderManager)senderManager;
            defaultSenderManager.setQueueNameToQueueInfos(inQueueInfos);
            defaultSenderManager.setBufferManager(defaultBufferManager);
            defaultSenderManager.startUp();
            this.registerLifeCycle(defaultSenderManager);
            frameLog.info("# (3/5)启动SenderManager OK #", new Object[0]);
            DefaultHandlerManager handlerManager = (DefaultHandlerManager)DefaultHandlerManager.getInstance();
            handlerManager.setQueueNameToQueueInfos(inQueueInfos);
            handlerManager.setBufferManager(defaultBufferManager);
            handlerManager.setConfigManager(configManager);
            handlerManager.setContext(this);
            handlerManager.startUp();
            this.registerLifeCycle(handlerManager);
            frameLog.info("# (4/5)启动HandlerManager OK #", new Object[0]);
            DefaultFetcherManager fetcherManager = (DefaultFetcherManager)DefaultFetcherManager.getInstance();
            fetcherManager.setQueueNameToQueueInfos(inQueueInfos);
            fetcherManager.setBufferManager(defaultBufferManager);
            fetcherManager.startUp();
            this.registerLifeCycle(fetcherManager);
            frameLog.info("# (5/5)启动FetcherManager OK #", new Object[0]);
            this.isStarted = true;

            while(this.isRunning) {
                try {
                    Thread.currentThread();
                    Thread.sleep(2000L);
                } catch (InterruptedException var12) {
                    var12.printStackTrace();
                }
            }

            frameLog.info("# 启动框架各组件(5) OK #", new Object[0]);
        }

    }

    public static void main(String[] args) throws InterruptedException {
        PrintUtil.printNs4();
        PrintUtil.printJVM();
        String filePath = System.getProperty("configfile");
        if (filePath == null) {
            throw new RuntimeException("请指定配置文件位置");
        } else {
            frameLog.info("### TRANSPORTER FRAMEWORK START ###", new Object[0]);
            XmlAppTransporterContext appTransporterContext = (XmlAppTransporterContext)getInstance();
            appTransporterContext.setResoucePath(filePath);
            appTransporterContext.startUp();
            Runtime.getRuntime().addShutdownHook(new Thread(appTransporterContext));
            frameLog.debug("# 配置文件路径:{} #", new Object[]{filePath});
            frameLog.info("### TRANSPORTER FRAMEWORK START  OK ###", new Object[0]);
        }
    }

    public String getResoucePath() {
        return this.resoucePath;
    }

    public void setResoucePath(String resoucePath) {
        this.resoucePath = resoucePath;
    }

    public static synchronized TransporterContext getInstance() {
        if (self == null) {
            self = new XmlAppTransporterContext();
        }

        return self;
    }

    public void destroy() {
        Iterator iterator = this.lifeCycles.iterator();

        while(iterator.hasNext()) {
            LifeCycle lifeCycle = (LifeCycle)iterator.next();

            try {
                lifeCycle.destroy();
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        }

        this.isRunning = false;
        this.isStarted = false;
        self = null;
    }

    public void registerExceptionListener(String queueName, ExceptionListener listener) {
        this.exceptionListeners.put(queueName, listener);
    }

    public ExceptionListener getExceptionListener(String queueName) {
        return (ExceptionListener)this.exceptionListeners.get(queueName);
    }

    public void registerLifeCycle(LifeCycle lifeCycle) {
        this.lifeCycles.add(lifeCycle);
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        System.out.println("[Transporter] [开始关闭] [cost:" + (System.currentTimeMillis() - startTime) + "ms]");
        this.destroy();
        System.out.println("[Transporter] [关闭] [成功] [cost:" + (System.currentTimeMillis() - startTime) + "ms]");
    }
}
