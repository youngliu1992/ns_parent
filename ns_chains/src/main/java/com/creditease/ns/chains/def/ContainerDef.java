package com.creditease.ns.chains.def;

import java.util.List;
import java.util.Map;

public interface ContainerDef extends ElementDef {
    List<ElementDef> getChildren();

    ElementDef getElementDefById(String var1);

    Map<String, ElementDef> getLocalScope();
}
