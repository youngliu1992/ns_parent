package com.creditease.ns.transporter.chain.service;

import com.creditease.framework.work.ActionWorker;

public abstract class AbstractServiceChainBridge extends ActionWorker {
    protected String catalogId;

    public AbstractServiceChainBridge() {
    }

    public String getCatalogId() {
        return this.catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }
}
