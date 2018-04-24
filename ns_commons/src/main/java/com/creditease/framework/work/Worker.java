package com.creditease.framework.work;

import com.creditease.framework.exception.NSException;
import com.creditease.framework.pojo.ServiceMessage;

public interface Worker {
    void doWork(ServiceMessage var1) throws NSException;
}
