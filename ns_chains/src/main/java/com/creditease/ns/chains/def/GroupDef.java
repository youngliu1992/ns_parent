package com.creditease.ns.chains.def;

import org.w3c.dom.Element;

public class GroupDef extends AbstractContainerDef {
    public GroupDef() {
    }

    public void init(Element element) throws Exception {
        framLog.info("# 初始化GroupDef id:{} #", new Object[]{this.id});
        super.init(element);
        framLog.info("# 初始化GroupDef id:{} OK #", new Object[]{this.id});
    }

    public void handle() {
    }

    public void postInit() throws Exception {
        super.postInit();
        if (this.id == null || this.id.trim().length() < 1) {
            throw new Exception("必须为Group指定一个id");
        }
    }
}
