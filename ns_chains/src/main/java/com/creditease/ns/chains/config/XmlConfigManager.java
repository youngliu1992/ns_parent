package com.creditease.ns.chains.config;

import com.creditease.framework.util.XMLUtil;
import com.creditease.ns.chains.constants.ChainConstants;
import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.chains.def.CatalogsDef;
import com.creditease.ns.chains.def.DefFactory;
import com.creditease.ns.chains.start.ChainLauncher;
import com.creditease.ns.log.NsLog;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlConfigManager implements ConfigManager {
    protected static NsLog loggerWrapper;
    protected String resourcePath;
    protected static XmlConfigManager xmlConfigManager;
    protected GlobalScope globalScope;
    protected String logPrefix = "[XmlConfigManager] ";
    protected Map<String, Long> filesToLastModifiedTime;
    protected static String AUTORELOAD_KEY;
    private List<File> configFiles = new ArrayList();

    protected XmlConfigManager() {
    }

    public static synchronized XmlConfigManager getInstance() throws Exception {
        if (xmlConfigManager == null) {
            xmlConfigManager = new XmlConfigManager();
        }

        return xmlConfigManager;
    }

    public void startUp() throws Exception {
        this.globalScope = GlobalScope.getInstance();
        loggerWrapper.info("# 加载配置文件 #", new Object[0]);
        loggerWrapper.debug("# 配置文件路径:{} #", new Object[]{this.resourcePath});
        this.filesToLastModifiedTime = new ConcurrentHashMap();
        File configFile = new File(this.resourcePath);
        if (configFile.isDirectory()) {
            this.loadDirConfig(configFile);
        } else {
            this.loadFileConfig(configFile);
        }

        loggerWrapper.info("# 加载配置文件 OK #", new Object[0]);
        List<CatalogsDef> allCatalogsDefs = this.globalScope.getAllCatalogsDef();
        Iterator iterator = allCatalogsDefs.iterator();

        while(iterator.hasNext()) {
            CatalogsDef catalogsDef = (CatalogsDef)iterator.next();
            catalogsDef.postInit();
        }

        if (System.getProperty(AUTORELOAD_KEY) != null && System.getProperty(AUTORELOAD_KEY).equals("true")) {
            loggerWrapper.info("# 启动自动加载心跳线程 #", new Object[0]);
            loggerWrapper.debug("# 心跳间隔时长:{}ms #", new Object[]{ChainConstants.HEART_BEAT_INTERVAL_SECONDS});
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleWithFixedDelay(new XmlConfigManager.HeartBeatThread(), ChainConstants.HEART_BEAT_INTERVAL_SECONDS, ChainConstants.HEART_BEAT_INTERVAL_SECONDS, TimeUnit.MILLISECONDS);
            loggerWrapper.info("# 启动自动加载心跳线程 OK #", new Object[0]);
        }

    }

    public void destroy() {
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    protected void loadDirConfig(File dir) throws Exception {
        FileFilter fileFilter = new XmlConfigManager.XmlAndDirFileFilter();
        File[] files = dir.listFiles(fileFilter);

        for(int i = 0; i < files.length; ++i) {
            File file = files[i];
            this.loadFileConfig(file);
        }

    }

    protected void loadFileConfig(File file) throws Exception {
        long startTime = System.currentTimeMillis();
        if (file.isDirectory()) {
            this.loadDirConfig(file);
        } else {
            loggerWrapper.debug("# 解析配置文件{} #", new Object[]{file.getAbsolutePath()});
            Document doc = XMLUtil.load(file);
            Element root = doc.getDocumentElement();
            CatalogsDef catalogsDef = (CatalogsDef)DefFactory.createElmentDef(root);
            catalogsDef.setFilePath(file.getAbsolutePath());
            this.globalScope.register(catalogsDef, catalogsDef.getLocalScope());
            this.globalScope.registerCatalogsDef(catalogsDef);
            this.configFiles.add(file);
            this.filesToLastModifiedTime.put(file.getAbsolutePath(), file.lastModified());
            loggerWrapper.debug("# 解析配置文件{} OK #", new Object[]{file.getAbsolutePath()});
        }
    }

    protected void reloadFile(File file) throws Exception {
        long startTime = System.currentTimeMillis();
        loggerWrapper.info("# 自动重载配置文件{} (3) #", new Object[]{file.getAbsolutePath()});
        Document tempDoc = null;

        try {
            tempDoc = XMLUtil.load(file);
        } catch (Exception var10) {
            loggerWrapper.error("# (1/3)分析配置文件 X #", var10);
            return;
        }

        loggerWrapper.debug("# (1/3)分析配置文件 √ #", new Object[0]);
        Element root = tempDoc.getDocumentElement();
        CatalogsDef tempCatalogsDef = null;

        try {
            tempCatalogsDef = (CatalogsDef)DefFactory.createElmentDef(root);
        } catch (Exception var9) {
            loggerWrapper.debug("# (2/3)解析Catalogs所有元素 X #", new Object[]{var9});
            return;
        }

        loggerWrapper.debug("# (2/3)解析Catalogs所有元素 √ #", new Object[0]);
        tempCatalogsDef.setFilePath(file.getAbsolutePath());
        this.globalScope.reRegister(tempCatalogsDef, tempCatalogsDef.getLocalScope());
        this.globalScope.registerCatalogsDef(tempCatalogsDef);
        tempCatalogsDef.postInit();
        loggerWrapper.debug("# (3/3)注册catalogs到全局域中 √ #", new Object[0]);
        loggerWrapper.info("# 自动重载配置文件{} OK #", new Object[]{file.getAbsolutePath()});
    }

    protected void clearOldElementDefsOf(CatalogsDef catalogsDef) {
    }

    protected void initConfig() {
    }

    static {
        loggerWrapper = ChainLauncher.framLog;
        xmlConfigManager = null;
        AUTORELOAD_KEY = "autoreload";
    }

    class HeartBeatThread implements Runnable {
        HeartBeatThread() {
        }

        public void run() {
            long var1 = System.currentTimeMillis();

            try {
                this.checkExistedFiles(XmlConfigManager.this.configFiles);
                List<File> newestFiles = this.getAllFiles(XmlConfigManager.this.resourcePath);
                Iterator iterator = newestFiles.iterator();

                while(iterator.hasNext()) {
                    File file = (File)iterator.next();
                    boolean isFound = false;
                    long newLastModified = file.lastModified();
                    Iterator iterator2 = XmlConfigManager.this.configFiles.iterator();

                    while(iterator2.hasNext()) {
                        File existedFile = (File)iterator2.next();
                        long oldLastModified = ((Long)XmlConfigManager.this.filesToLastModifiedTime.get(existedFile.getAbsolutePath())).longValue();
                        if (existedFile.getAbsolutePath().equals(file.getAbsolutePath())) {
                            isFound = true;
                            if (oldLastModified != newLastModified && file.exists()) {
                                XmlConfigManager.this.filesToLastModifiedTime.put(file.getAbsolutePath(), newLastModified);
                                XmlConfigManager.this.reloadFile(file);
                            }
                        }
                    }

                    if (!isFound && file.exists()) {
                        XmlConfigManager.this.filesToLastModifiedTime.put(file.getAbsolutePath(), newLastModified);
                        XmlConfigManager.this.reloadFile(file);
                    }
                }

                XmlConfigManager.this.configFiles = newestFiles;
            } catch (Exception var13) {
                var13.printStackTrace();
            }

        }

        private List<File> getAllFiles(String resourcePath) {
            File configFile = new File(resourcePath);
            List<File> files = new ArrayList();
            if (configFile.isDirectory()) {
                File[] cFiles = configFile.listFiles();

                for(int i = 0; i < cFiles.length; ++i) {
                    files.addAll(this.getAllFiles(cFiles[i].getAbsolutePath()));
                }
            } else {
                files.add(configFile);
            }

            return files;
        }

        private void checkExistedFiles(List<File> configFiles) {
            Iterator iterator = configFiles.iterator();

            while(iterator.hasNext()) {
                File file = (File)iterator.next();
                if (!file.exists()) {
                    CatalogsDef catalogsDef = XmlConfigManager.this.globalScope.getCatalogsDefByFilePath(file.getAbsolutePath());
                    if (catalogsDef != null) {
                        catalogsDef.writeLock();

                        try {
                            XmlConfigManager.this.globalScope.unRegisterCatalogsDef(catalogsDef);
                            XmlConfigManager.this.globalScope.unRegister(catalogsDef, catalogsDef.getLocalScope());
                        } catch (Exception var9) {
                            var9.printStackTrace();
                        } finally {
                            catalogsDef.writeUnLock();
                        }
                    }

                    iterator.remove();
                }
            }

        }
    }

    class XmlAndDirFileFilter implements FileFilter {
        XmlAndDirFileFilter() {
        }

        public boolean accept(File pathname) {
            String tmp = pathname.getName().toLowerCase();
            return tmp.endsWith(".xml") || pathname.isDirectory();
        }
    }
}
