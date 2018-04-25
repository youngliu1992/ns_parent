package com.creditease.ns.chains.def;

import com.creditease.framework.util.XMLUtil;
import com.creditease.ns.chains.chain.Command;
import com.creditease.ns.chains.context.GlobalScope;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

public class CommandDef extends AbstractAtomicElementDef {
    private String queueName;
    private String className;

    public String getQueueName() {
        return this.queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public CommandDef() {
    }

    public void init(Element e) throws Exception {
        framLog.info("# 初始化CommandDef id:{} #", new Object[]{this.id});
        super.init(e);
        String className = XMLUtil.getAttributeAsString(e, "class", (Map)null);
        this.id = this.id;
        this.className = className;
        this.desc = this.desc;
        framLog.info("# 初始化CommandDef id:{} desc:{} className:{} tagName:{} OK #", new Object[]{this.getClass().getSimpleName(), this.id, this.desc, className, e.getTagName()});
    }

    public void handle() {
    }

    public void postInit() throws Exception {
        long startTime = System.currentTimeMillis();

        try {
            Class<Command> cl = Class.forName(this.className);
            if (!Command.class.isAssignableFrom(cl)) {
                throw new Exception("指定的Class[" + this.className + "]必须实现Command接口");
            }
        } catch (ClassNotFoundException var4) {
            framLog.error("{}postInit失败,找不到对应的class id:{} desc:{} className:{}", new Object[]{this.getClass().getSimpleName(), this.id, this.desc, this.className, System.currentTimeMillis() - startTime, var4});
            throw new Exception("没有找到对应的Class[" + this.className + "]");
        }
    }

    public void tranverse(List targetList) throws Exception {
        long startTime = System.currentTimeMillis();
        if (targetList == null) {
            flowLog.error("# 组装执行链节点{}失败,要放入的节点列表为null id:{} desc:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), System.currentTimeMillis() - startTime});
            throw new NullPointerException("传入的list为null");
        } else {
            try {
                Command command = this.getCommand();
                targetList.add(command);
                flowLog.trace("# 组装执行链节点{}成功 id:{} desc:{} targetsize:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), targetList.size(), System.currentTimeMillis() - startTime});
            } catch (Exception var5) {
                flowLog.error("# 组装执行链节点{}失败,出现异常 id:{} desc:{} cost:{}ms #", new Object[]{this.getClass().getSimpleName(), this.getId(), this.getDesc(), System.currentTimeMillis() - startTime});
                throw var5;
            }
        }
    }

    public Command getCommand() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<Command> cl = Class.forName(this.className);
        return !GlobalScope.hasSpring ? (Command)cl.newInstance() : (Command)GlobalScope.getInstance().getSpringPlugin().getBeanByClassName(cl);
    }
}
