package com.creditease.ns.chains.context;

import com.creditease.ns.chains.config.ConfigManager;
import com.creditease.ns.chains.def.CatalogDef;
import com.creditease.ns.chains.def.CatalogsDef;
import com.creditease.ns.chains.def.ElementDef;
import com.creditease.ns.framework.spring.SpringPlugin;
import com.creditease.ns.log.NsLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalScope {
    private static NsLog flowLog = NsLog.getFlowLog("全局域状态日志打印", "主要用作调试");
    private ConcurrentHashMap<String, CatalogsDef> globalIdToElement;
    private ConcurrentHashMap<String, CatalogsDef> filePathToCatalogs;
    private String logPrefix = "[GlobalScope] ";
    public static boolean hasSpring = false;
    private SpringPlugin springPlugin;
    private ConfigManager configManager;
    private static GlobalScope globalScope = null;

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    private GlobalScope() {
    }

    public static synchronized GlobalScope getInstance() {
        if (globalScope == null) {
            globalScope = new GlobalScope();
        }

        return globalScope;
    }

    public void registerCatalogsDef(CatalogsDef catalogsDef) {
        this.filePathToCatalogs.put(catalogsDef.getFilePath(), catalogsDef);
        flowLog.debug("# 注册catalogs到全局域成功 {} {} {}", new Object[]{catalogsDef.getFilePath(), catalogsDef.getId(), this.filePathToCatalogs.size()});
    }

    public void unRegisterCatalogsDef(CatalogsDef catalogsDef) {
        this.filePathToCatalogs.remove(catalogsDef.getFilePath());
        flowLog.debug("# 移除catalogs 成功 {} {} {}", new Object[]{catalogsDef.getFilePath(), catalogsDef.getId(), this.filePathToCatalogs.size()});
    }

    private void register(ElementDef elementDef) throws Exception {
        if (!(elementDef instanceof CatalogsDef)) {
            ElementDef def = (ElementDef)this.globalIdToElement.putIfAbsent(elementDef.getId(), (CatalogsDef)elementDef.getParentElementDef());
            if (def != null) {
                flowLog.error("注册ElementDef 失败 存在相同的id {} {}", new Object[]{elementDef.getId(), elementDef.getDesc()});
                throw new Exception("全局域中出现相同id[" + elementDef.getId() + "]的元素，请更换id");
            } else {
                flowLog.debug("注册ElementDef 成功 {} {}", new Object[]{elementDef.getId(), elementDef.getDesc()});
            }
        }
    }

    private void reRegister(ElementDef elementDef) throws Exception {
        if (!(elementDef instanceof CatalogsDef)) {
            this.unRegister(elementDef);
            ElementDef def = (ElementDef)this.globalIdToElement.putIfAbsent(elementDef.getId(), (CatalogsDef)elementDef.getParentElementDef());
            if (def != null) {
                flowLog.error("注册ElementDef 失败 存在相同的id {} {}", new Object[]{elementDef.getId(), elementDef.getDesc()});
                throw new Exception("全局域中出现相同id[" + elementDef.getId() + "]的元素，请更换id");
            } else {
                flowLog.debug("注册ElementDef 成功 {} {}", new Object[]{elementDef.getId(), elementDef.getDesc()});
            }
        }
    }

    private void unRegister(ElementDef elementDef) {
        this.globalIdToElement.remove(elementDef.getId());
        flowLog.debug("卸载ElementDef 成功 {} {}", new Object[]{elementDef.getId(), elementDef.getDesc()});
    }

    public void register(CatalogsDef catalogsDef, Map<String, ElementDef> map) throws Exception {
        Iterator iterator = map.keySet().iterator();

        while(iterator.hasNext()) {
            String elementId = (String)iterator.next();
            this.register((ElementDef)map.get(elementId));
        }

    }

    public void reRegister(CatalogsDef catalogsDef, Map<String, ElementDef> map) throws Exception {
        catalogsDef.writeLock();

        try {
            this.unRegister(catalogsDef, map);
            Iterator iterator = map.keySet().iterator();

            while(iterator.hasNext()) {
                String elementId = (String)iterator.next();
                this.reRegister((ElementDef)map.get(elementId));
            }
        } finally {
            catalogsDef.writeUnLock();
        }

    }

    public void unRegister(CatalogsDef catalogsDef, Map<String, ElementDef> map) throws Exception {
        Iterator iterator = map.keySet().iterator();

        while(iterator.hasNext()) {
            String elementId = (String)iterator.next();
            this.unRegister((ElementDef)map.get(elementId));
        }

    }

    public void init() {
        this.globalIdToElement = new ConcurrentHashMap();
        this.filePathToCatalogs = new ConcurrentHashMap();
    }

    public CatalogDef getCatalogDef(String catalogId) {
        CatalogsDef catalogsDef = (CatalogsDef)this.globalIdToElement.get(catalogId);
        if (catalogsDef != null) {
            catalogsDef.readLock();

            try {
                ElementDef elementDef = catalogsDef.getElementDefById(catalogId);
                CatalogDef var4;
                if (elementDef == null) {
                    var4 = null;
                    return var4;
                }

                if (elementDef instanceof CatalogDef) {
                    var4 = (CatalogDef)elementDef;
                    return var4;
                }

                flowLog.debug("获取CatalogDef 失败 获取元素不是CatalogDef {} {} {}", new Object[]{elementDef.getId(), elementDef.getDesc(), elementDef.getClass().getSimpleName()});
            } finally {
                catalogsDef.readUnLock();
            }
        }

        return null;
    }

    public List<CatalogsDef> getAllCatalogsDef() {
        return new ArrayList(this.filePathToCatalogs.values());
    }

    public ElementDef getGlobalScopeElement(String elementId) {
        CatalogsDef catalogsDef = (CatalogsDef)this.globalIdToElement.get(elementId);
        if (catalogsDef != null) {
            catalogsDef.readLock();
            ElementDef elementDef = catalogsDef.getElementDefById(elementId);
            catalogsDef.readUnLock();
            return elementDef;
        } else {
            return null;
        }
    }

    public SpringPlugin getSpringPlugin() {
        return this.springPlugin;
    }

    public void setSpringPlugin(SpringPlugin springPlugin) {
        this.springPlugin = springPlugin;
    }

    public CatalogsDef getCatalogsDefByFilePath(String filePath) {
        return (CatalogsDef)this.filePathToCatalogs.get(filePath);
    }
}
