package com.creditease.ns.chains.chain;

import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.chains.def.CatalogDef;
import com.creditease.ns.chains.exchange.Exchanger;

public class DefaultChainDispatcher extends AbstractChain {
    public static DefaultChainDispatcher self;
    protected GlobalScope globalScope;

    public void doChain(Exchanger exchanger) throws Exception {
        long startTime = System.currentTimeMillis();
        String catalogId = (String)exchanger.getParameter("NS_CHAIN_DEFAULT_CATALOG_DISPATCHER_KEY");
        CatalogDef catalogDef = this.globalScope.getCatalogDef(catalogId);
        if (catalogDef != null) {
            Chain chain = ChainBuilder.build(catalogDef);
            chain.doChain(exchanger);
        } else {
            flowLog.error("没有获取到对应的Catalog catalogId:{}", new Object[]{catalogId});
            throw new Exception("[" + catalogId + "]没有找到对应的Catalog服务");
        }
    }

    public static synchronized DefaultChainDispatcher getInstance() {
        if (self == null) {
            self = new DefaultChainDispatcher();
            self.init();
        }

        return self;
    }

    protected DefaultChainDispatcher() {
    }

    private void init() {
        this.globalScope = GlobalScope.getInstance();
    }

    public void add(Command command) {
        throw new UnsupportedOperationException("不支持此方法");
    }

    public String getLogStr() {
        return "[执行链分发器]";
    }
}
