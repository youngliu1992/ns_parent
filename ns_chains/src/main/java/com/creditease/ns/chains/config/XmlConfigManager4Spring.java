package com.creditease.ns.chains.config;

import com.creditease.framework.util.XMLUtil;
import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.chains.def.CatalogsDef;
import com.creditease.ns.chains.def.DefFactory;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlConfigManager4Spring extends XmlConfigManager {
    public XmlConfigManager4Spring() {
    }

    public void startUp() throws Exception {
        this.globalScope = GlobalScope.getInstance();
        loggerWrapper.info("# 加载配置文件流 #", new Object[0]);
        loggerWrapper.debug("# 配置文件路径:{} #", new Object[]{this.resourcePath});
        this.filesToLastModifiedTime = new ConcurrentHashMap();
        this.loadInputStreams4ConfigOnlyInSpring();
        loggerWrapper.info("# 加载配置文件流 OK #", new Object[0]);
        List<CatalogsDef> allCatalogsDefs = this.globalScope.getAllCatalogsDef();
        Iterator iterator = allCatalogsDefs.iterator();

        while(iterator.hasNext()) {
            CatalogsDef catalogsDef = (CatalogsDef)iterator.next();
            catalogsDef.postInit();
        }

    }

    public static synchronized XmlConfigManager getInstance() throws Exception {
        if (xmlConfigManager == null) {
            xmlConfigManager = new XmlConfigManager4Spring();
            loggerWrapper.debug("# 创建XmlConfigManager4Spring成功 #", new Object[0]);
        }

        return xmlConfigManager;
    }

    protected void loadInputStreams4ConfigOnlyInSpring() throws Exception {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = null;
        if (this.resourcePath.indexOf(".") != -1) {
            resources = resolver.getResources("classpath:" + this.resourcePath);
        } else if (this.resourcePath.endsWith("/")) {
            resources = resolver.getResources("classpath:" + this.resourcePath + "*");
        } else {
            resources = resolver.getResources("classpath:" + this.resourcePath + "/*");
        }

        if (resources != null && resources.length > 0) {
            for(int i = 0; i < resources.length; ++i) {
                loggerWrapper.debug("#  解析配置文件流{} #", new Object[]{resources[i].getURL()});
                InputStream inputStream = resources[i].getInputStream();
                this.loadInputStream4Config(inputStream, resources[i].getURL().toString());
                inputStream.close();
            }
        }

    }

    protected void loadInputStream4Config(InputStream inputStream, String resourceUrl) throws Exception {
        Document doc = XMLUtil.load(inputStream, "utf-8");
        Element root = doc.getDocumentElement();
        CatalogsDef catalogsDef = (CatalogsDef)DefFactory.createElmentDef(root);
        catalogsDef.setFilePath(resourceUrl);
        this.globalScope.register(catalogsDef, catalogsDef.getLocalScope());
        this.globalScope.registerCatalogsDef(catalogsDef);
        loggerWrapper.debug("# 解析配置文件流{} OK #", new Object[0]);
    }
}
