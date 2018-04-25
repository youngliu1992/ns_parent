package com.creditease.ns.transporter.chain.service;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.ns.chains.chain.Chain;
import com.creditease.ns.chains.chain.ChainFactory;
import com.creditease.ns.chains.exchange.DefaultExchanger;
import com.creditease.ns.log.spi.TransporterLog;
import com.creditease.ns.mq.model.Header;
import com.creditease.ns.transporter.chain.adapter.ServiceMessageExchangerAdapter;
import java.util.HashMap;
import java.util.Map;

public class DefaultServiceChainBridge extends AbstractServiceChainBridge {
    public DefaultServiceChainBridge() {
    }

    public void doWork(ServiceMessage serviceMessage) throws NSException {
        long startTime = System.currentTimeMillis();
        if (this.catalogId != null && this.catalogId.trim().length() >= 1) {
            Header header = serviceMessage.getHeader();
            DefaultExchanger exchanger = null;

            try {
                Map<String, Object> requestScope = new HashMap();
                requestScope.put("NS_CHAIN_DEFAULT_CATALOG_DISPATCHER_KEY", this.catalogId);
                exchanger = new DefaultExchanger(requestScope);
                ServiceMessageExchangerAdapter serviceMessageAdapter = new ServiceMessageExchangerAdapter();
                serviceMessageAdapter.setServiceMessage(serviceMessage);
                serviceMessageAdapter.setExchanger(exchanger);
                exchanger.setExchange("NS_CHAIN_DEFAULT_SERVICEMESSAGE_KEY", serviceMessageAdapter);
                Chain chain = ChainFactory.getDefaultChain();
                chain.doChain(exchanger);
                TransporterLog.logSystemDebug("[中转消息] [成功] [{}] [{}] [{}] [cost:{}ms]", new Object[]{header, exchanger, this.catalogId, System.currentTimeMillis() - startTime});
            } catch (NSException var9) {
                TransporterLog.logSystemError("[中转消息] [失败] [内部执行链停止] [出现异常] [{}] [{}] [{}] [cost:{}ms]", new Object[]{header, exchanger, this.catalogId, System.currentTimeMillis() - startTime, var9});
                throw var9;
            } catch (Exception var10) {
                TransporterLog.logSystemError("[中转消息] [失败] [出现异常] [{}] [{}] [{}] [cost:{}ms]", new Object[]{header, exchanger, this.catalogId, System.currentTimeMillis() - startTime, var10});
                throw new RuntimeException("出现未知异常");
            }
        } else {
            throw new NSException("没有指定本地任务链");
        }
    }
}
