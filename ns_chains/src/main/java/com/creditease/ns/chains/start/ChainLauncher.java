package com.creditease.ns.chains.start;

import com.creditease.framework.util.FileUtils;
import com.creditease.ns.chains.config.XmlConfigManager;
import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.framework.spring.GenSpringPlugin;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.log.util.PrintUtil;

public class ChainLauncher implements LifeCycle {
    public static NsLog framLog = NsLog.getFramLog("启动NS_CHAINS", "启动NS_CHAINS");
    private static ChainLauncher chainLauncher = null;
    private static String CHAIN_CONFIG_KEY = "chainconfig";
    private String resourcePath;
    private boolean parseConfigFileInSpring = false;

    private ChainLauncher() {
    }

    public static synchronized ChainLauncher getInstance() throws Exception {
        if (chainLauncher == null) {
            chainLauncher = new ChainLauncher();
        }

        return chainLauncher;
    }

    public void startUp() throws Exception {
        long startTime = System.currentTimeMillis();
        boolean isSpring = false;
        GenSpringPlugin springPlugin = null;

        try {
            framLog.debug("# 加载spring插件 #", new Object[0]);
            Class.forName("org.springframework.beans.factory.BeanFactory");
            isSpring = true;
            springPlugin = new GenSpringPlugin();
            springPlugin.init();
            framLog.debug("# 加载spring插件 OK #", new Object[0]);
        } catch (ClassNotFoundException var9) {
            ;
        }

        GlobalScope.hasSpring = isSpring;
        GlobalScope chainsContext = GlobalScope.getInstance();
        if (isSpring) {
            chainsContext.setSpringPlugin(springPlugin);
        }

        chainsContext.init();

        try {
            XmlConfigManager configManager = XmlConfigManager.getInstance();
            configManager.setResourcePath(this.resourcePath);
            configManager.startUp();
            chainsContext.setConfigManager(configManager);
        } catch (Exception var8) {
            framLog.error("# 解析配置出现异常 #", var8);
            throw var8;
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

    public boolean isParseConfigFileInSpring() {
        return this.parseConfigFileInSpring;
    }

    public void setParseConfigFileInSpring(boolean parseConfigFileInSpring) {
        this.parseConfigFileInSpring = parseConfigFileInSpring;
    }

    public static void start() throws Exception {
        long startTime = System.currentTimeMillis();
        PrintUtil.printNs4();
        framLog.info("###启动NS_CHAINS框架###", new Object[0]);
        ChainLauncher chainLauncher = getInstance();
        String filePath = System.getProperty(CHAIN_CONFIG_KEY);
        if (filePath == null || filePath.trim().length() < 1) {
            filePath = "nschainconfig";
        }

        filePath = FileUtils.convertToAbsolutePath(filePath);
        chainLauncher.setResourcePath(filePath);
        chainLauncher.startUp();
        framLog.debug("# 配置文件路径:{} #", new Object[]{filePath});
        framLog.info("###启动NS_CHAINS框架 OK，共耗时:{}ms###", new Object[]{System.currentTimeMillis() - startTime});
    }
}
