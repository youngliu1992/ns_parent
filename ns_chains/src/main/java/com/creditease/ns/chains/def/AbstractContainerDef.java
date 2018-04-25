package com.creditease.ns.chains.def;

import com.creditease.framework.util.StringUtil;
import com.creditease.framework.util.XMLUtil;
import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.chains.util.ElementCheckUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

public abstract class AbstractContainerDef implements ContainerDef {
    protected List<ElementDef> children;
    protected Map<String, ElementDef> localScope;
    protected String id;
    protected String desc;
    protected ElementDef parentElementDef;
    protected String tagName;

    public AbstractContainerDef() {
    }

    public void init(Element element) throws Exception {
        ElementCheckUtil.checkElement(element);
        this.localScope = new HashMap();
        this.children = new ArrayList();
        String id = element.getAttribute("id");
        this.id = id;
        String desc = element.getAttribute("desc");
        this.desc = desc;
        this.tagName = element.getTagName();
        Element[] elements = XMLUtil.getChildren(element);

        for(int i = 0; i < elements.length; ++i) {
            ElementDef elementDef = DefFactory.createElmentDef(elements[i]);
            this.register(elementDef);
        }

    }

    public List<ElementDef> getChildren() {
        return this.children;
    }

    public void register(ElementDef elementDef) throws Exception {
        long startTime = System.currentTimeMillis();
        if (elementDef.getId() != null && elementDef.getId().trim().length() > 0) {
            if (this.localScope.containsKey(elementDef.getId())) {
                throw new Exception("本地域中出现同名对象，请确认声明的id" + elementDef.getId() + "没有重复");
            }

            this.localScope.put(elementDef.getId(), elementDef);
        }

        this.children.add(elementDef);
        elementDef.setParentElementDef(this);
    }

    public String getId() {
        return this.id;
    }

    public String getDesc() {
        return this.desc;
    }

    public void tranverse(List targetList) throws Exception {
        long startTime = System.currentTimeMillis();
        if (targetList == null) {
            flowLog.error("# 组装执行链节点{}失败,要放入的节点列表为null id:{} desc:{} children:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), this.children.size(), System.currentTimeMillis() - startTime});
            throw new NullPointerException("传入的list为null");
        } else {
            Iterator iterator = this.children.iterator();

            while(iterator.hasNext()) {
                ElementDef elementDef = (ElementDef)iterator.next();
                elementDef.tranverse(targetList);
            }

        }
    }

    public ElementDef find(String refId) {
        long startTime = System.currentTimeMillis();
        ElementDef elementDef = (ElementDef)this.localScope.get(refId);
        if (elementDef == null) {
            if (this.parentElementDef != null) {
                try {
                    flowLog.trace("{}查找引用 本地域没有找到 往上层域查找 id:{} desc:{} children:{} localScope:{} refId:{} parentclass:{} parentId:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), this.children.size(), this.localScope.size(), refId, this.parentElementDef.getClass().getSimpleName(), this.parentElementDef.getId(), System.currentTimeMillis() - startTime});
                    elementDef = this.parentElementDef.find(refId);
                } catch (Exception var6) {
                    flowLog.error("{}查找引用 本地域没有找到 往上层域查找 失败 出现未知异常 id:{} desc:{} children:{} localScope:{} refId:{} parentclass:{} parentId:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), this.children.size(), this.localScope.size(), refId, this.parentElementDef.getClass().getSimpleName(), this.parentElementDef.getId(), System.currentTimeMillis() - startTime, var6});
                    return elementDef;
                }
            } else {
                elementDef = GlobalScope.getInstance().getGlobalScopeElement(refId);
            }
        }

        flowLog.trace("{}查找引用 成功 id:{} desc:{} children:{} localScope:{} refId:{} elementDef:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), this.children.size(), this.localScope.size(), refId, elementDef, System.currentTimeMillis() - startTime});
        return elementDef;
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

    public boolean isCanBeRefered() {
        return true;
    }

    public void setParentElementDef(ElementDef elementDef) {
        this.parentElementDef = elementDef;
    }

    public ElementDef getParentElementDef() {
        return this.parentElementDef;
    }

    public void postInit() throws Exception {
        long startTime = System.currentTimeMillis();
        if (this.children != null && this.children.size() >= 1) {
            Iterator iterator = this.children.iterator();

            ElementDef elementDef;
            while(iterator.hasNext()) {
                elementDef = (ElementDef)iterator.next();
                if (elementDef.getId() != null && elementDef.getId().trim().length() > 0 && elementDef.getId().equals(this.getId())) {
                    throw new Exception("容器类元素内部不能有和容器本身id" + this.getId() + "相同名称的元素");
                }
            }

            if (this.parentElementDef != null) {
                if (!(this.parentElementDef instanceof ContainerDef)) {
                    throw new Exception("非容器类元素" + this.parentElementDef.getId() + "内部不能放任何元素");
                }
            } else if (!(this instanceof CatalogsDef)) {
                throw new Exception("只有Catalogs元素可以独立存在");
            }

            iterator = this.children.iterator();

            while(iterator.hasNext()) {
                elementDef = (ElementDef)iterator.next();

                try {
                    framLog.trace("{}postInit 调用children{} id:{} desc:{} children:{} localScope:{} childId:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), elementDef.getClass().getSimpleName(), this.getId(), this.getDesc(), this.children.size(), this.localScope.size(), elementDef.getId(), System.currentTimeMillis() - startTime});
                    elementDef.postInit();
                } catch (Exception var6) {
                    flowLog.error("{}postInit 调用children{} 失败 出现未知异常 id:{} desc:{} children:{} localScope:{} childId:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), elementDef.getClass().getSimpleName(), this.getId(), this.getDesc(), this.children.size(), this.localScope.size(), elementDef.getId(), System.currentTimeMillis() - startTime, var6});
                    throw new Exception("postInit出现异常", var6);
                }
            }

            framLog.trace("{}postInit 成功 id:{} desc:{} children:{} localScope:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), this.children.size(), this.localScope.size(), System.currentTimeMillis() - startTime});
        } else {
            throw new Exception("容器类元素不能为空，必须指定子元素");
        }
    }

    public ElementDef getElementDefById(String id) {
        return (ElementDef)this.localScope.get(id);
    }

    public Map<String, ElementDef> getLocalScope() {
        return this.localScope;
    }

    public void checkLoopEmbedElement(List<ElementDef> parentElementDefs) throws Exception {
        if (parentElementDefs.contains(this)) {
            this.buildListStatusForExceptionDesc(parentElementDefs);
            throw new Exception("探测到循环嵌套元素[" + this.tagName + "],id为[" + this.getId() + "] 循环状态:loopStatus");
        } else {
            parentElementDefs.add(this);
            Iterator iterator = this.children.iterator();

            while(iterator.hasNext()) {
                List<ElementDef> lst = new ArrayList();
                lst.addAll(parentElementDefs);
                ElementDef elementDef = (ElementDef)iterator.next();
                elementDef.checkLoopEmbedElement(lst);
            }

        }
    }

    public boolean equals(Object obj) {
        ElementDef def = (ElementDef)obj;
        return !StringUtil.isEmpty(this.id) ? this.id.equals(def.getId()) : super.equals(obj);
    }

    public String buildListStatusForExceptionDesc(List<ElementDef> parentElementDefs) {
        StringBuilder builder = new StringBuilder();
        Iterator iterator = parentElementDefs.iterator();

        while(iterator.hasNext()) {
            ElementDef elementDef = (ElementDef)iterator.next();
            builder.append(elementDef.getId() + " -> ");
        }

        builder.append(this.getId());
        return builder.toString();
    }
}
