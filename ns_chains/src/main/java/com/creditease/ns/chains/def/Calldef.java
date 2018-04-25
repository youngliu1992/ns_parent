package com.creditease.ns.chains.def;

import com.creditease.ns.chains.context.GlobalScope;
import java.util.List;
import org.w3c.dom.Element;

public class Calldef extends AbstractAtomicElementDef {
    private String refCatalog;

    public Calldef() {
    }

    public void init(Element e) throws Exception {
        framLog.info("# 初始化Calldef #", new Object[0]);
        super.init(e);
        this.refCatalog = e.getAttribute("refCatalog");
        if (this.refCatalog != null && this.refCatalog.trim().length() >= 1) {
            framLog.info("# 初始化Calldef OK desc:{} refCatalog:{} #", new Object[]{this.getClass().getSimpleName(), this.id, this.desc, this.refCatalog});
        } else {
            throw new Exception("call元素必须执行refCatalog");
        }
    }

    public void tranverse(List targetList) throws Exception {
        long startTime = System.currentTimeMillis();
        if (targetList == null) {
            flowLog.error("# 组装执行链节点{}失败,传入的targetList为null id:{} desc:{} refCatalog:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), this.refCatalog, System.currentTimeMillis() - startTime});
            throw new NullPointerException("传入的list为null");
        } else {
            CatalogDef catalogDef = GlobalScope.getInstance().getCatalogDef(this.refCatalog);
            if (catalogDef == null) {
                flowLog.error("# 组装执行链节点{}失败,指定的refCatalog不存在 id:{} desc:{} refCatalog:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), this.id, this.desc, this.refCatalog, System.currentTimeMillis() - startTime});
                throw new Exception("call引用的catalog" + this.refCatalog + "不存在");
            } else {
                catalogDef.tranverse(targetList);
                flowLog.trace("# 组装执行链节点{}成功 id:{} desc:{} targetList:{} refCatalog:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), this.id, this.desc, targetList.size(), this.refCatalog, System.currentTimeMillis() - startTime});
            }
        }
    }

    public void postInit() throws Exception {
        CatalogDef catalogDef = GlobalScope.getInstance().getCatalogDef(this.refCatalog);
        if (catalogDef == null) {
            framLog.error("{}postInit失败,因为对应的catalogRef不存在 id:{} desc:{} refCatalog:{}", new Object[]{this.getClass().getSimpleName(), this.id, this.desc, this.refCatalog});
            throw new Exception("call引用的catalog" + this.refCatalog + "不存在");
        } else {
            this.checkParentElementDef();
        }
    }

    public ElementDef getParentElementDef() {
        return this.parentElementDef;
    }

    protected void checkParentElementDef() throws Exception {
        super.checkParentElementDef();
        if (!(this.parentElementDef instanceof ChainDef)) {
            throw new Exception("call标签只能放在chain标签中");
        }
    }

    public void checkLoopEmbedElement(List<ElementDef> parentElementDefs) throws Exception {
        CatalogDef catalogDef = GlobalScope.getInstance().getCatalogDef(this.refCatalog);
        if (catalogDef != null) {
            try {
                catalogDef.checkLoopEmbedElement(parentElementDefs);
            } catch (Exception var5) {
                throw new Exception("探测到循环嵌套元素[refCatalog],refCatalog为[" + this.refCatalog + "],循环状态:" + catalogDef.buildListStatusForExceptionDesc(parentElementDefs));
            }
        }

    }
}
