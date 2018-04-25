package com.creditease.ns.transporter.fetch;

import com.creditease.framework.exception.ThreadUncaughtExceptionHandler;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.transporter.buffer.BufferManager;
import com.creditease.ns.transporter.config.InQueueInfo;
import com.creditease.ns.transporter.context.XmlAppTransporterContext;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultFetcherManager implements FetcherManager, LifeCycle {
    private boolean isStarted = false;
    private boolean isStartedFetch = false;
    private static int DEFAULT_FETCHER_NUM_PER_QUEUE = 1;
    private Map<String, InQueueInfo> queueNameToQueueInfos;
    private BufferManager bufferManager;
    private static FetcherManager self = new DefaultFetcherManager();
    private Map<String, ExecutorService> queueNameToFetcherExecutors = new LinkedHashMap();
    private static NsLog frameLog;

    public DefaultFetcherManager() {
    }

    public void init() {
        self = this;
    }

    public synchronized void startUp() {
        if (!this.isStarted) {
            this.startFetch();
            this.isStarted = true;
        }

    }

    public void destroy() {
        this.stopFetch();
        frameLog.info("fetcherManager关闭 成功 {} {}", new Object[]{this.queueNameToFetcherExecutors.size(), this.queueNameToQueueInfos.size()});
    }

    public synchronized void startFetch() {
        if (!this.isStartedFetch) {
            Iterator it = this.queueNameToQueueInfos.keySet().iterator();

            while(it.hasNext()) {
                String queueName = (String)it.next();
                InQueueInfo inQueueInfo = (InQueueInfo)this.queueNameToQueueInfos.get(queueName);
                ExecutorService scheduler = Executors.newCachedThreadPool(new DefaultFetcherManager.CustomThreadNameThreadFactory("f", queueName));

                for(int i = 0; i < inQueueInfo.getFetcherNum(); ++i) {
                    DefaultFetcher fetcher = new DefaultFetcher();
                    fetcher.setQueueName(queueName);
                    fetcher.setBufferManager(this.bufferManager);
                    scheduler.execute(fetcher);
                }

                this.queueNameToFetcherExecutors.put(queueName, scheduler);
            }

            this.isStartedFetch = true;
        }

    }

    public void stopFetch() {
        Iterator it = this.queueNameToQueueInfos.keySet().iterator();

        while(it.hasNext()) {
            String queueName = (String)it.next();
            ((ExecutorService)this.queueNameToFetcherExecutors.get(queueName)).shutdownNow();
            this.queueNameToFetcherExecutors.remove(queueName);
        }

    }

    public Map<String, InQueueInfo> getQueueNameToQueueInfos() {
        return this.queueNameToQueueInfos;
    }

    public void setQueueNameToQueueInfos(Map<String, InQueueInfo> queueNameToQueueInfos) {
        this.queueNameToQueueInfos = Collections.unmodifiableMap(queueNameToQueueInfos);
    }

    public static synchronized FetcherManager getInstance() {
        if (self == null) {
            self = new DefaultFetcherManager();
        }

        return self;
    }

    public BufferManager getBufferManager() {
        return this.bufferManager;
    }

    public void setBufferManager(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    static {
        frameLog = XmlAppTransporterContext.frameLog;
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
