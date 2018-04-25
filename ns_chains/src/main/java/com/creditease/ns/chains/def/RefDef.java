package com.creditease.ns.chains.def;

import com.creditease.framework.util.XMLUtil;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

public class RefDef extends AbstractAtomicElementDef {
    private String ref;

    public RefDef() {
    }

    public void handle() {
    }

    public void init(Element e) throws Exception {
        framLog.info("# 初始化RefDef ref:{} #", new Object[]{this.ref});
        this.ref = XMLUtil.getAttributeAsString(e, "ref", (Map)null);
        super.init(e);
        framLog.info("# 初始化RefDef ref:{} OK #", new Object[]{this.ref});
    }

    public void postInit() throws Exception {
        long startTime = System.currentTimeMillis();

        try {
            ElementDef reference = this.find(this.ref);
            if (reference == null) {
                framLog.error("{}postInit失败 没有找到对应的元素 id:{} desc:{} ref:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), this.id, this.desc, this.ref, System.currentTimeMillis() - startTime});
                throw new Exception("没有找到对应[" + this.ref + "]的元素,请确认ref指定的id正确");
            }
        } catch (Exception var4) {
            framLog.error("{}postInit失败,出现异常 id:{} desc:{} ref:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), this.id, this.desc, this.ref, System.currentTimeMillis() - startTime, var4});
            throw new Exception("初始化后没有找到refId[" + this.ref + "]对应的引用元素，请确认refId正确");
        }

        framLog.trace("{}postInit成功 id:{} desc:{} ref:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), this.id, this.desc, this.ref, System.currentTimeMillis() - startTime});
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
            flowLog.error("# 组装执行链节点{}失败,要放入的节点列表为null ref:{} desc:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.ref, this.getDesc(), System.currentTimeMillis() - startTime});
            throw new NullPointerException("传入的list为null");
        } else {
            ElementDef reference = this.find(this.ref);
            if (reference == null) {
                flowLog.error("# 组装执行链节点{}失败,指定的引用对象为null ref:{} desc:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.ref, this.getDesc(), System.currentTimeMillis() - startTime});
                throw new IllegalStateException("[" + this.ref + "]出现reference为null的情况");
            } else {
                if (reference instanceof CommandDef) {
                    CommandDef commandDef = (CommandDef)reference;
                    targetList.add(commandDef.getCommand());
                    flowLog.trace("# 组装执行链节点{}成功 ref:{} desc:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.ref, this.getDesc(), System.currentTimeMillis() - startTime});
                } else {
                    reference.tranverse(targetList);
                    flowLog.trace("# 组装执行链节点{},引用了非command对象,继续往后遍历 ref:{} desc:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.ref, this.getDesc(), System.currentTimeMillis() - startTime});
                }

            }
        }
    }

    public ElementDef find(String refId) throws Exception {
        long startTime = System.currentTimeMillis();
        ElementDef localRefer = this.parentElementDef.find(refId);
        flowLog.trace("查找引用对象{} 成功 id:{} desc:{} refId:{} localRefer:{} cost:{}ms", new Object[]{this.getClass().getSimpleName(), this.id, this.desc, refId, localRefer, System.currentTimeMillis() - startTime});
        return localRefer;
    }

    public void checkLoopEmbedElement(List<ElementDef> parentElementDefs) throws Exception {
        ElementDef reference = this.find(this.ref);
        if (reference != null) {
            try {
                reference.checkLoopEmbedElement(parentElementDefs);
            } catch (Exception var5) {
                AbstractContainerDef abstractContainerDef = (AbstractContainerDef)reference;
                throw new Exception("探测到循环嵌套元素[ref],refId为[" + this.ref + "],循环状态:" + abstractContainerDef.buildListStatusForExceptionDesc(parentElementDefs));
            }
        }

    }
}
