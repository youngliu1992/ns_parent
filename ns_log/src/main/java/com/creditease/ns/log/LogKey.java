package com.creditease.ns.log;

import org.slf4j.MDC;

public class LogKey {
    public LogKey() {
    }

    public static String getTotalKey() {
        StringBuilder keySb = new StringBuilder();
        String uuid = MDC.get("uuid");
        if (uuid == null) {
            uuid = "+";
        }

        keySb.append("[").append(uuid);
        String primaryKey = MDC.get("primaryKey");
        if (primaryKey != null) {
            keySb.append(",").append(primaryKey);
        }

        String subPrimaryKey = MDC.get("subprimaryKey");
        if (subPrimaryKey != null) {
            keySb.append(",").append(subPrimaryKey);
        }

        keySb.append("]");
        return keySb.toString();
    }
}
