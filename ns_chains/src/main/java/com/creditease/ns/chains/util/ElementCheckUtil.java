package com.creditease.ns.chains.util;

import com.creditease.framework.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

public class ElementCheckUtil {
    public static List<String> elementsOfNotRequiredDescAttribute = new ArrayList<String>() {
        {
            this.add("chain");
        }
    };

    public ElementCheckUtil() {
    }

    public static void checkElement(Element e) throws Exception {
        String tagName = e.getTagName();
        if (!elementsOfNotRequiredDescAttribute.contains(tagName) && StringUtil.isEmpty(e.getAttribute("desc"))) {
            throw new Exception("NsChains配置的[" + e.getTagName() + "]元素[" + (StringUtil.isEmpty(e.getAttribute("id")) ? "" : e.getAttribute("id")) + "]必须配置desc属性!");
        }
    }
}
