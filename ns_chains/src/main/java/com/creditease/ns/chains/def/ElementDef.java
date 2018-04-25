package com.creditease.ns.chains.def;

import com.creditease.ns.chains.start.ChainLauncher;
import com.creditease.ns.log.NsLog;
import java.util.List;
import org.w3c.dom.Element;

public interface ElementDef {
    NsLog framLog = ChainLauncher.framLog;
    NsLog flowLog = NsLog.getFlowLog("元素动态加载过程", "解析的元素被动态加载");

    String getId();

    void handle() throws Exception;

    void init(Element var1) throws Exception;

    String getDesc();

    void tranverse(List var1) throws Exception;

    ElementDef find(String var1) throws Exception;

    void postInit() throws Exception;

    boolean isCanBeRefered();

    void setParentElementDef(ElementDef var1);

    ElementDef getParentElementDef();

    void checkLoopEmbedElement(List<ElementDef> var1) throws Exception;

    boolean isSuperiorElement(ElementDef var1);
}
