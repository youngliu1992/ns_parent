package com.creditease.ns.transporter.constants;

import com.creditease.ns.log.NsLog;

public class TransporterConstants {
    public static final String DEFAULT_TRANSPORTER_SERVICEMESSAGE_KEY = "ns_transporter_servicemessage_key";
    public static final String DEFAULT_TRANSPORTER_SERVICECLASSNAME = "com.creditease.ns.transporter.chain.service.DefaultServiceChainBridge";
    public static final String DEFAULT_TRANSPORTER_SPRING_SERVICECLASSNAME = "com.creditease.ns.transporter.chain.service.DefaultSpringServiceChainBridge";
    public static final NsLog WHOLE_FLOW_LOG = NsLog.getFlowLog("TransporterFlow", "TransporterFlow");

    public TransporterConstants() {
    }
}