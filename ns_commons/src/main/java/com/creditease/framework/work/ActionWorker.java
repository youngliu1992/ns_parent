//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.creditease.framework.work;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.exception.StopChainException;
import com.creditease.framework.exception.StopException;
import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.RetInfo;
import com.creditease.framework.scope.SystemOutKey;

public abstract class ActionWorker implements Worker {
    public ActionWorker() {
    }

    public abstract void doWork(ServiceMessage var1) throws NSException;

    public static void stop(ServiceMessage serviceMessage, RetInfo retInfo) throws StopException {
        StopChainException stopException = new StopChainException(retInfo.getMsg());

        try {
            serviceMessage.setOut(SystemOutKey.RETURN_CODE, retInfo);
        } catch (NSException var4) {
            var4.printStackTrace();
        }

        throw stopException;
    }
}
