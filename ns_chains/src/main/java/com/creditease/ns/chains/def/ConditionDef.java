package com.creditease.ns.chains.def;

import com.creditease.framework.util.XMLUtil;
import com.creditease.ns.chains.chain.AbstractConditionaleCommand;
import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.chains.util.ExpUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

public class ConditionDef extends AbstractContainerDef {
    private String cond;
    protected String className;

    public ConditionDef() {
    }

    public void init(Element e) throws Exception {
        framLog.info("# 初始化ConditionDef #", new Object[0]);
        this.cond = XMLUtil.getAttributeAsString(e, "cond", (Map)null);
        this.className = e.getAttribute("class");
        super.init(e);
        if (this.cond == null) {
            framLog.error("# 初始化ConditionDef因为缺少cond条件失败,className:{} #", new Object[]{this.getClass().getSimpleName()});
            throw new Exception("condition元素必须指明cond条件");
        } else {
            framLog.info("# 初始化ConditionDef  desc:{} cond:{} className:{} tagName:{} OK #", new Object[]{this.getClass().getSimpleName(), this.id, this.desc, this.cond, this.className, e.getTagName()});
        }
    }

    public void tranverse(List targetList) throws Exception {
        long startTime = System.currentTimeMillis();
        if (targetList == null) {
            flowLog.error("# 组装执行链节点{}失败,要放入的节点列表为null id:{} desc:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), System.currentTimeMillis() - startTime});
            throw new NullPointerException("传入的list为null");
        } else {
            AbstractConditionaleCommand curCommand = null;
            if (this.className != null && this.className.trim().length() >= 1) {
                Class<AbstractConditionaleCommand> cl = Class.forName(this.className);
                curCommand = (AbstractConditionaleCommand)cl.newInstance();
                flowLog.trace("# 组装执行链节点{},使用自己定义的command id:{} desc:{} cond:{} className:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), System.currentTimeMillis() - startTime});
            } else {
                curCommand = new AbstractConditionaleCommand() {
                    public void setCond(String cond) {
                        this.cond = cond;
                    }

                    public boolean canExecute(String cond, Exchanger exchanger) {
                        Map map = exchanger.getExchangeScope();
                        boolean isCan = false;

                        try {
                            isCan = ExpUtil.checkCond(cond, map);
                        } catch (Exception var6) {
                            ElementDef.flowLog.error("条件判断,检查是否符合配置的条件 map中不包含对应的属性 cond:{} exchanger:{}", new Object[]{cond, exchanger, var6});
                        }

                        return isCan;
                    }

                    public void setDesc(String desc) {
                        super.setDesc(desc);
                    }
                };
                flowLog.trace("# 组装执行链节点{},没有定义自己的class 使用默认的匿名command id:{} desc:{} cond:{} className:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), System.currentTimeMillis() - startTime});
            }

            List<Command> lst = new ArrayList();
            Iterator iterator = this.children.iterator();

            while(iterator.hasNext()) {
                ElementDef elementDef = (ElementDef)iterator.next();
                elementDef.tranverse(lst);
                flowLog.trace("# 组装执行链节点{} 调用cond元素的children id:{} desc:{} cond:{} className:{} curElementId:{} curElementClass:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), elementDef.getId(), elementDef.getClass().getSimpleName(), System.currentTimeMillis() - startTime});
            }

            curCommand.setCommands(lst);
            curCommand.setCond(this.cond);
            curCommand.setDesc(this.desc);
            targetList.add(curCommand);
            flowLog.trace("# 组装执行链节点{}成功 id:{} desc:{} cond:{} className:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), System.currentTimeMillis() - startTime});
        }
    }

    public void handle() {
    }

    public void postInit() throws Exception {
        super.postInit();
        if (this.className != null && this.className.trim().length() > 0) {
            Class cl = Class.forName(this.className);
            if (!AbstractConditionaleCommand.class.isAssignableFrom(cl)) {
                throw new Exception("实现自定义的条件判断类，必须继承AbstractConditionaleCommand抽象类");
            }
        }

    }
}
