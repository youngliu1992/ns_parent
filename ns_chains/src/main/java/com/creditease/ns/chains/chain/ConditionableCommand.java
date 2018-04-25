package com.creditease.ns.chains.chain;

import com.creditease.ns.chains.exchange.Exchanger;

public interface ConditionableCommand extends Command {
    boolean canExecute(String var1, Exchanger var2);

    String getCond();

    void setCond(String var1);
}