package com.creditease.ns.chains.def;

import com.creditease.ns.chains.util.ElementCheckUtil;
import java.util.List;
import org.w3c.dom.Element;

public abstract class AbstractAtomicElementDef implements AtomicElementDef {
    protected String id;
    protected String desc;
    protected ElementDef parentElementDef;

    public AbstractAtomicElementDef() {
    }

    public String getId() {
        return this.id;
    }

    public void init(Element e) throws Exception {
        ElementCheckUtil.checkElement(e);
        this.id = e.getAttribute("id");
        this.desc = e.getAttribute("desc");
    }

    public void handle() {
    }

    public String getDesc() {
        return this.desc;
    }

    public boolean isCanBeRefered() {
        return true;
    }

    public void setParentElementDef(ElementDef elementDef) {
        this.parentElementDef = elementDef;
    }

    public ElementDef getParentElementDef() {
        return this.parentElementDef;
    }

    public ElementDef find(String refId) throws Exception {
        return (ElementDef)(refId.equals(this.id) ? this : this.parentElementDef.find(refId));
    }

    protected void checkParentElementDef() throws Exception {
        if (this.parentElementDef != null) {
            if (!(this.parentElementDef instanceof ContainerDef)) {
                throw new Exception("非容器类元素[" + this.parentElementDef.getId() + "]内部不能放任何元素");
            }
        } else {
            throw new Exception("非容器元素必须存放于容器元素内");
        }
    }

    public void checkLoopEmbedElement(List<ElementDef> parentElementDefs) throws Exception {
    }

    public boolean isSuperiorElement(ElementDef elementDef) {
        boolean isSuperior = false;
        if (this.parentElementDef != null) {
            if (this.parentElementDef.equals(elementDef)) {
                isSuperior = true;
            } else {
                isSuperior = this.parentElementDef.isSuperiorElement(elementDef);
            }
        }

        return isSuperior;
    }
}
