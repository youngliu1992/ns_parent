package com.creditease.ns.chains.chain;

import com.creditease.ns.chains.def.CatalogDef;
import java.util.ArrayList;
import java.util.List;

public class ChainBuilder {
    public ChainBuilder() {
    }

    public static Chain build(CatalogDef catalogDef) throws Exception {
        List<Command> commands = new ArrayList();
        catalogDef.tranverse(commands);
        DefaultChain chain = new DefaultChain();
        chain.init();
        chain.addAll(commands);
        Chain.flowLog.debug("ChainBuilder构建工作链 成功 catalogDef:{} chain中的命令数:{}", new Object[]{catalogDef.getId(), commands.size()});
        return chain;
    }
}