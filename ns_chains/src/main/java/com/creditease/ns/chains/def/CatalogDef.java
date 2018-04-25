package com.creditease.ns.chains.def;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Element;

public class CatalogDef extends AbstractContainerDef {
    public void init(Element element) throws Exception {
        framLog.info("# 初始化CatalogDef id:{} #", new Object[]{this.id});
        super.init(element);
        framLog.info("# 初始化CatalogDef id:{} OK #", new Object[]{this.id});
    }

    public void handle() {
    }

    public void postInit() throws Exception {
        super.postInit();
        if (this.id != null && this.id.trim().length() >= 1) {
            boolean chainDefFinded = false;
            Iterator iterator = this.children.iterator();

            while(iterator.hasNext()) {
                ElementDef elementDef = (ElementDef)iterator.next();
                if (elementDef instanceof ChainDef) {
                    if (chainDefFinded) {
                        throw new Exception("一个Catalog中只能放一个Chain定义");
                    }

                    chainDefFinded = true;
                }

                List<ElementDef> elementDefs = new ArrayList();
                elementDefs.add(this);
                elementDef.checkLoopEmbedElement(elementDefs);
            }

            if (!chainDefFinded) {
                throw new Exception("Catalog中必须放入一个Chain定义");
            }
        } else {
            throw new Exception("必须为Catalog指定id");
        }
    }

    public boolean isCanBeRefered() {
        return false;
    }

    public CatalogDef() {
    }

    public void tranverse(List targetList) throws Exception {
        long startTime = System.currentTimeMillis();
        if (targetList == null) {
            flowLog.error("# 组装执行链节点{}失败,要放入的节点列表为null id:{} desc:{} children:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), this.children.size(), System.currentTimeMillis() - startTime});
            throw new NullPointerException("传入的list为null");
        } else {
            ElementDef elementDef;
            for(Iterator iterator = this.children.iterator(); iterator.hasNext(); flowLog.trace("遍历{} 遍历到{} 成功 id:{} desc:{} children:{} targetList:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), elementDef.getClass().getSimpleName(), elementDef.getId(), elementDef.getDesc(), this.children.size(), targetList.size(), System.currentTimeMillis() - startTime})) {
                elementDef = (ElementDef)iterator.next();
                if (elementDef instanceof ChainDef) {
                    elementDef.tranverse(targetList);
                }
            }

            flowLog.trace("# 组装执行链节点{}成功 id:{} desc:{} children:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), this.children.size(), System.currentTimeMillis() - startTime});
        }
    }
}
