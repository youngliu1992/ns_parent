package com.creditease.ns.transporter.send;

import com.creditease.framework.exception.ThreadUncaughtExceptionHandler;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.transporter.buffer.BufferManager;
import com.creditease.ns.transporter.config.InQueueInfo;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultSenderManager implements SenderManager, LifeCycle {
    private boolean isStarted = false;
    private boolean isStartedSend = false;
    private static int DEFAULT_SENDER_NUM_PER_QUEUE = 1;
    private Map<String, InQueueInfo> queueNameToQueueInfos;
    private BufferManager bufferManager;
    private static SenderManager self = new DefaultSenderManager();
    private Map<String, ExecutorService> queueNameToSenderExecutors = new LinkedHashMap();

    public DefaultSenderManager() {
    }

    public void init() {
        self = this;
    }

    public synchronized void startUp() {
        if (!this.isStarted) {
            this.startSend();
            this.isStarted = true;
        }

    }

    public void destroy() {
        Iterator it = this.queueNameToQueueInfos.keySet().iterator();

        while(it.hasNext()) {
            String queueName = (String)it.next();
            ((ExecutorService)this.queueNameToSenderExecutors.get(queueName)).shutdownNow();
            this.queueNameToSenderExecutors.remove(queueName);
        }

        this.isStartedSend = false;
        this.isStarted = false;
    }

    public synchronized void startSend() {
        if (!this.isStartedSend) {
            Iterator it = this.queueNameToQueueInfos.keySet().iterator();

            while(it.hasNext()) {
                String queueName = (String)it.next();
                InQueueInfo inQueueInfo = (InQueueInfo)this.queueNameToQueueInfos.get(queueName);
                ExecutorService scheduler = Executors.newCachedThreadPool(new DefaultSenderManager.CustomThreadNameThreadFactory("s", queueName));

                for(int i = 0; i < inQueueInfo.getSenderNum(); ++i) {
                    DefaultSender sender = new DefaultSender();
                    sender.setQueueName(queueName);
                    sender.setBufferManager(this.bufferManager);
                    scheduler.execute(sender);
                }

                this.queueNameToSenderExecutors.put(queueName, scheduler);
            }

            this.isStartedSend = true;
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

    public Map<String, ExecutorService> getQueueNameToSenderExecutors() {
        return this.queueNameToSenderExecutors;
    }

    public void setQueueNameToSenderExecutors(Map<String, ExecutorService> queueNameToSenderExecutors) {
        this.queueNameToSenderExecutors = queueNameToSenderExecutors;
    }

    public static synchronized SenderManager getInstance() {
        if (self == null) {
            self = new DefaultSenderManager();
        }

        return self;
    }

    static class CustomThreadNameThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        CustomThreadNameThreadFactory(String threadNamePrefix, String queuename) {
            SecurityManager s = System.getSecurityManager();
            this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = threadNamePrefix + "-" + queuename + "-" + poolNumber.getAndIncrement() + "-t-";
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
