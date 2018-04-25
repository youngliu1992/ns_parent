package com.creditease.ns.transporter.fetch;

public interface Fetcher {
    void fetch();

    void stop() throws Exception;
}
