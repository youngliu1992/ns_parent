package com.creditease.ns.chains.chain;

import com.creditease.ns.chains.exchange.Exchanger;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractConditionaleCommand implements ConditionableCommand {
    protected List<Command> commands;
    protected String cond;
    protected String desc;

    public AbstractConditionaleCommand() {
    }

    public void doCommand(Exchanger exchanger) throws Exception {
        if (this.canExecute(this.cond, exchanger)) {
            Iterator iterator = this.commands.iterator();

            while(iterator.hasNext()) {
                Command command = (Command)iterator.next();
                command.doCommand(exchanger);
            }
        } else {
            this.doEmpty();
        }

    }

    protected void doEmpty() {
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public String getCond() {
        return this.cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLogStr() {
        return "[执行条件判断命令] [" + this.cond + "] [" + (this.commands == null ? 0 : this.commands.size()) + "] [" + this.desc + "]";
    }

    public boolean isNotBreak() {
        return false;
    }
}
