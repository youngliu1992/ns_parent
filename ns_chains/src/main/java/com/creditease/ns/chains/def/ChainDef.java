package com.creditease.ns.chains.def;

import org.w3c.dom.Element;

public class ChainDef extends AbstractContainerDef {
    public ChainDef() {
    }

    public void init(Element element) throws Exception {
        framLog.info("# 初始化ChainDef id:{} #", new Object[]{this.id});
        super.init(element);
        framLog.info("# 初始化ChainDef id:{} OK #", new Object[]{this.id});
    }

    public void handle() {
    }

    public boolean isCanBeRefered() {
        return true;
    }
}
