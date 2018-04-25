package com.creditease.ns.chains.chain;

import com.creditease.ns.chains.exchange.Exchanger;
import com.creditease.ns.log.NsLog;
import java.util.Map;
import org.mvel2.MVEL;

public class TransporterCondParser extends AbstractConditionaleCommand {
    private static NsLog flowLog;

    public TransporterCondParser() {
    }

    public boolean canExecute(String cond, Exchanger exchanger) {
        Map exchangeScope = exchanger.getExchangeScope();

        try {
            boolean isCan = ((Boolean)MVEL.eval(cond, exchangeScope)).booleanValue();
            flowLog.debug("业务链条件判断 成功 是否符合条件:{} cond:{} exchangeScope:{}", new Object[]{isCan, cond, exchangeScope});
            return isCan;
        } catch (SecurityException var5) {
            flowLog.error("业务链条件判断 失败 安全异常 cond:{} exchangeScope:{}", new Object[]{false, cond, exchangeScope});
            var5.printStackTrace();
        } catch (IllegalArgumentException var6) {
            flowLog.error("条件判断 失败 传入参数不合法 cond:{} exchangeScope:{}", new Object[]{cond, exchangeScope});
            var6.printStackTrace();
        }

        return false;
    }

    static {
        flowLog = Chain.flowLog;
    }
}
