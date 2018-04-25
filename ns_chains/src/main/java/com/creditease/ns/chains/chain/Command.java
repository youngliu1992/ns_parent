package com.creditease.ns.chains.chain;

import com.creditease.ns.chains.exchange.Exchanger;

public interface Command {
    String logPrefix = "[Command] ";

    void doCommand(Exchanger var1) throws Exception;

    boolean isNotBreak();

    String getLogStr();
}