package com.creditease.ns.chains.constants;

import com.creditease.ns.chains.def.Calldef;
import com.creditease.ns.chains.def.CatalogDef;
import com.creditease.ns.chains.def.CatalogsDef;
import com.creditease.ns.chains.def.ChainDef;
import com.creditease.ns.chains.def.CommandDef;
import com.creditease.ns.chains.def.ConditionDef;
import com.creditease.ns.chains.def.ElementDef;
import com.creditease.ns.chains.def.GroupDef;
import com.creditease.ns.chains.def.RefDef;
import com.creditease.ns.log.spi.LoggerWrapper;
import java.util.concurrent.ConcurrentHashMap;

public class DefConstants {
    private static LoggerWrapper loggerWrapper;
    public static ConcurrentHashMap<String, Class> customElements;
    public static final String commandElementName = "command";
    public static final String groupElementName = "group";
    public static final String catalogElementName = "catalog";
    public static final String chainElementName = "chain";
    public static final String refElementName = "ref";
    public static final String conditionElementName = "condition";
    public static final String catalogsElementName = "catalogs";
    public static final String callElementName = "call";
    public static final String idString = "id";
    public static final String descString = "desc";

    public DefConstants() {
    }

    private static void putCustomElment(String tagName, Class elementDef) throws Exception {
        Class cl = (Class)customElements.putIfAbsent(tagName, elementDef);
        if (cl != null) {
            loggerWrapper.logDebug("[DefConstants] [注册自定义标签] [失败] [已存在同名标签] [{}] [{}]", new Object[]{tagName, elementDef.getCanonicalName()});
            throw new Exception("此tagName[" + tagName + "]已经注册过了,请重新换一个tagName");
        } else {
            loggerWrapper.logDebug("[DefConstants] [注册自定义标签] [成功] [{}] [{}]", new Object[]{tagName, elementDef.getCanonicalName()});
        }
    }

    public static void registerCustomElement(String tagName, Class elementDef) throws Exception {
        if (!ElementDef.class.isAssignableFrom(elementDef)) {
            loggerWrapper.logDebug("[DefConstants] [注册自定义标签] [失败] [注册的Class必须实现ElementDef接口] [{}] [{}]", new Object[]{tagName, elementDef.getCanonicalName()});
            throw new Exception("此class[" + elementDef.getCanonicalName() + "]没有实现ElementDef接口");
        } else {
            putCustomElment(tagName, elementDef);
        }
    }

    static {
        loggerWrapper = LoggerConstants.DEF_LOGGER;
        customElements = new ConcurrentHashMap();
        customElements.put("command", CommandDef.class);
        customElements.put("group", GroupDef.class);
        customElements.put("catalog", CatalogDef.class);
        customElements.put("chain", ChainDef.class);
        customElements.put("ref", RefDef.class);
        customElements.put("condition", ConditionDef.class);
        customElements.put("catalogs", CatalogsDef.class);
        customElements.put("call", Calldef.class);
    }
}
