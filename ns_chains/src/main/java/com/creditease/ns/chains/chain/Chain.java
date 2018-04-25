package com.creditease.ns.chains.chain;

import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.log.NsLog;

public interface Chain extends Command {
    NsLog flowLog = NsLog.getFlowLog("NsChainFlow", "NsChainFlow");

    void doChain(Exchanger var1) throws Exception;

    void add(Command var1);
}