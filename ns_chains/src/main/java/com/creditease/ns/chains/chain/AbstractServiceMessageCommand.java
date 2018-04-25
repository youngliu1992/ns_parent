package com.creditease.ns.chains.chain;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.exception.StopException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.RetInfo;
import com.creditease.framework.work.ActionWorker;
import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.log.NsLog;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractServiceMessageCommand implements Command {
    static NsLog flowLog = NsLog.getFlowLog("NsChainFlow", "NsChainFlow");
    private ThreadLocal<Object> notBreakChain = new ThreadLocal();

    public AbstractServiceMessageCommand() {
    }

    public void doCommand(Exchanger exchanger) throws Exception {
        ServiceMessage serviceMessage = (ServiceMessage)exchanger.getExchange("NS_CHAIN_DEFAULT_SERVICEMESSAGE_KEY");
        this.doService(serviceMessage);
    }

    public abstract void doService(ServiceMessage var1) throws NSException;

    public abstract String getLogStr();

    public void stop(ServiceMessage serviceMessage, RetInfo retInfo) throws StopException {
        ActionWorker.stop(serviceMessage, retInfo);
    }

    public void continueCurrentChain() {
        Object isNotBreakChain = this.notBreakChain.get();
        Boolean isNotBreak = true;
        Map<String, Boolean> isNotBreakChainMap = null;
        if (isNotBreakChain == null) {
            isNotBreakChainMap = new HashMap();
            this.notBreakChain.set(isNotBreakChainMap);
        } else {
            isNotBreakChainMap = (Map)isNotBreakChain;
        }

        ((Map)isNotBreakChainMap).put(this.getClass().getName(), isNotBreak);
        flowLog.debug("# 设置不中断标记 OK classname:{} isnotbreak:{} #", new Object[]{this.getClass().getName(), isNotBreak});
    }

    public boolean isNotBreak() {
        Object isNotBreakChain = this.notBreakChain.get();
        Boolean isNotBreak = false;
        if (isNotBreakChain == null) {
            isNotBreak = false;
        } else {
            Map<String, Boolean> isNotBreakChainMap = (Map)isNotBreakChain;
            isNotBreak = (Boolean)isNotBreakChainMap.get(this.getClass().getName());
            if (isNotBreak == null) {
                isNotBreak = false;
            }
        }

        flowLog.debug("# 获取不中断标记 OK classname:{} isnotbreak:{} #", new Object[]{this.getClass().getName(), isNotBreak});
        return isNotBreak.booleanValue();
    }
}
