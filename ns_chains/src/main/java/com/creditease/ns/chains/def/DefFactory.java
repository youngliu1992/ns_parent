package com.creditease.ns.chains.def;

import com.creditease.ns.chains.constants.DefConstants;
import com.creditease.ns.chains.start.ChainLauncher;
import com.creditease.ns.log.NsLog;
import org.w3c.dom.Element;

public class DefFactory {
    private static NsLog loggerWrapper;

    public DefFactory() {
    }

    public static ElementDef createElmentDef(Element e) throws Exception {
        Class elementDefClass = (Class)DefConstants.customElements.get(e.getTagName());
        if (elementDefClass != null) {
            ElementDef elementDef = (ElementDef)elementDefClass.newInstance();
            elementDef.init(e);
            loggerWrapper.trace("构造{}并初始化 tag:{}", new Object[]{elementDefClass.getCanonicalName(), e.getTagName()});
            return elementDef;
        } else {
            throw new Exception("未识别的Element[" + e.getTagName() + "]");
        }
    }

    static {
        loggerWrapper = ChainLauncher.framLog;
    }
}
