package com.creditease.ns.chains.chain;

import com.creditease.framework.exception.NotStopException;
import com.creditease.framework.exception.StopException;
import com.creditease.ns.chains.exchange.Exchanger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultChain extends AbstractChain {
    protected List<Command> commands;

    public DefaultChain() {
    }

    public void doChain(Exchanger exchanger) throws Exception {
        long startTime = System.currentTimeMillis();
        flowLog.debug("# 执行业务工作链 commands数量:{} exchanger:{} #", new Object[]{this.commands.size(), exchanger});

        Command command;
        for(Iterator iterator = this.commands.iterator(); iterator.hasNext(); flowLog.trace("# 正在执行命令{} 成功 exchanger:{} cost:{}ms #", new Object[]{command.getLogStr(), exchanger, System.currentTimeMillis() - startTime})) {
            command = (Command)iterator.next();

            try {
                command.doCommand(exchanger);
                flowLog.info("# 执行命令[{}] OK #", new Object[]{command.getLogStr()});
            } catch (Exception var7) {
                if (var7 instanceof RuntimeException || var7 instanceof StopException || !(var7 instanceof NotStopException) && !command.isNotBreak()) {
                    flowLog.error("执行业务链出现错误需要中断 exchanger:{} {} [cost:{}ms]", new Object[]{exchanger, command.getLogStr(), System.currentTimeMillis() - startTime});
                    throw var7;
                }
            }
        }

        flowLog.debug("exchanger:{}", new Object[]{exchanger});
        flowLog.debug("# 执行业务工作链 OK commands数量:{} cost:{}ms #", new Object[]{this.commands.size(), System.currentTimeMillis() - startTime});
    }

    public void add(Command command) {
        this.commands.add(command);
    }

    public void addAll(List<Command> commands) {
        this.commands.addAll(commands);
    }

    public void init() {
        this.commands = new ArrayList();
    }

    public String getLogStr() {
        return "[执行默认链] [commands:" + (this.commands == null ? 0 : this.commands.size()) + "]";
    }
}
