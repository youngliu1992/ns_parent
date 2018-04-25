package com.creditease.ns.chains.def;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.w3c.dom.Element;

public class CatalogsDef extends AbstractContainerDef {
    private String filePath;
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public CatalogsDef() {
    }

    public void handle() {
    }

    public void init(Element element) throws Exception {
        framLog.info("# 初始化CatalogsDef id:{} #", new Object[0]);
        super.init(element);
        framLog.info("# 初始化CatalogsDef id:{} desc:{} children:{} OK #", new Object[]{this.getClass().getSimpleName(), this.id, this.desc, this.children.size()});
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getId() {
        return null;
    }

    public boolean isCanBeRefered() {
        return false;
    }

    public void readLock() {
        this.readWriteLock.readLock().lock();
    }

    public void readUnLock() {
        this.readWriteLock.readLock().unlock();
    }

    public void writeLock() {
        this.readWriteLock.writeLock().lock();
    }

    public void writeUnLock() {
        this.readWriteLock.writeLock().unlock();
    }
}
