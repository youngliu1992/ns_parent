package com.creditease.ns.log.converter;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.creditease.ns.log.spi.SimpleLogger;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.MDC;

public class NewlLineMessageConverter extends MessageConverter {
    private static String ip;
    private static String hostName;

    public NewlLineMessageConverter() {
    }

    public String convert(ILoggingEvent event) {
        String message = super.convert(event);
        if (message == null) {
            return "";
        } else if (!event.getLoggerName().equals(SimpleLogger.class.getName())) {
            return message;
        } else {
            boolean endWithEnter = message.endsWith("\n");
            StringTokenizer stringTokenizer = new StringTokenizer(message, "\r\n");
            int tokenCounts = stringTokenizer.countTokens();
            StringBuilder sb = new StringBuilder();
            String prefix = this.buildLogPrefix();

            for(int i = 1; stringTokenizer.hasMoreElements(); ++i) {
                String stringLine = (String)stringTokenizer.nextElement();
                if (stringLine.endsWith(" ")) {
                    sb.append("||").append(stringLine).append(prefix);
                } else {
                    sb.append("||").append(stringLine).append("||").append(prefix);
                }

                if (i < tokenCounts) {
                    sb.append("\r\n");
                } else if (endWithEnter) {
                    sb.append("\r\n");
                }
            }

            return sb.toString();
        }
    }

    private String buildLogPrefix() {
        StringBuilder stringBuilder = new StringBuilder();
        String prefix = "";
        String tail = "";
        this.logContentStart(stringBuilder, "txnId", prefix, tail);
        this.logContentAppend(stringBuilder, "orderId", prefix, tail);
        this.logContentAppend(stringBuilder, "systemOrderId", prefix, tail);
        this.logContentAppend(stringBuilder, "batchId", prefix, tail);
        this.logContentAppend(stringBuilder, "tradeStatus", prefix, tail);
        this.logContentAppend(stringBuilder, "responseCode", prefix, tail);
        this.logContentAppend(stringBuilder, "responseCodeMess", prefix, tail);
        this.logContentAppend(stringBuilder, "merchantId", prefix, tail);
        this.logContentAppend(stringBuilder, "channelMerchantId", prefix, tail);
        this.logContentAppend(stringBuilder, "channelName", prefix, tail);
        this.logContentAppend(stringBuilder, "payType", prefix, tail);
        this.logContentAppend(stringBuilder, "txnType", prefix, tail);
        this.logContentAppend(stringBuilder, "updateTime", prefix, tail);
        this.logContentAppend(stringBuilder, "remark", prefix, tail);
        this.logContentAppend(stringBuilder, "errorDesp", prefix, tail);
        this.logContentAppend(stringBuilder, "chkSts", prefix, tail);
        this.logContentAppend(stringBuilder, "operatorId", prefix, tail);
        this.logContentAppend(stringBuilder, "thirdHeadCode", prefix, tail);
        this.logContentAppend(stringBuilder, "thirdHeadMsg", prefix, tail);
        this.logContentAppend(stringBuilder, "thirdBodyCode", prefix, tail);
        this.logContentAppend(stringBuilder, "thirdBodyMsg", prefix, tail);
        if (ip == null) {
            try {
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                boolean isFindIp = false;
                String s = "e\\w+0";
                Pattern p = Pattern.compile(s);

                label54:
                do {
                    NetworkInterface item;
                    String devName;
                    do {
                        if (!networkInterfaces.hasMoreElements()) {
                            break label54;
                        }

                        item = (NetworkInterface)networkInterfaces.nextElement();
                        devName = item.getDisplayName();
                    } while(devName == null);

                    Matcher m = p.matcher(devName);
                    boolean isNeedDev = m.matches();
                    if (!item.isLoopback() && isNeedDev) {
                        Iterator i$ = item.getInterfaceAddresses().iterator();

                        while(i$.hasNext()) {
                            InterfaceAddress address = (InterfaceAddress)i$.next();
                            if (address.getAddress() instanceof Inet4Address) {
                                Inet4Address inet4Address = (Inet4Address)address.getAddress();
                                if (!inet4Address.isLoopbackAddress() && !inet4Address.isMulticastAddress()) {
                                    String ip = inet4Address.getHostAddress();
                                    ip = ip;
                                    stringBuilder.append("||");
                                    stringBuilder.append(prefix).append(ip).append(tail);
                                    isFindIp = true;
                                    if (hostName == null) {
                                        hostName = inet4Address.getHostName();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } while(!isFindIp);
            } catch (SocketException var16) {
                var16.printStackTrace();
                String ip = "unknownhost";
                stringBuilder.append("||");
                stringBuilder.append(prefix).append(ip).append(tail);
                return stringBuilder.toString();
            }
        } else {
            stringBuilder.append("||");
            stringBuilder.append(prefix).append(ip).append(tail);
        }

        stringBuilder.append("||");
        stringBuilder.append(prefix).append(UUID.randomUUID()).append(tail);
        if (hostName != null) {
            stringBuilder.append("||");
            stringBuilder.append(prefix).append(hostName).append(tail);
        }

        this.logContentAppend(stringBuilder, "chanId", prefix, tail);
        stringBuilder.append("||");
        stringBuilder.append(prefix).append("").append(tail);
        this.logContentAppend(stringBuilder, "sttlAmt", prefix, tail);
        this.logContentAppend(stringBuilder, "actualSttlAmt", prefix, tail);
        this.logContentAppend(stringBuilder, "splitFlg", prefix, tail);
        this.logContentAppend(stringBuilder, "procSts", prefix, tail);
        this.logContentAppend(stringBuilder, "notifyUrl", prefix, tail);
        this.logContentAppend(stringBuilder, "outTxnId", prefix, tail);
        this.logContentAppend(stringBuilder, "exTxnTd", prefix, tail);
        this.logContentAppend(stringBuilder, "captureTime", prefix, tail);
        this.logContentAppend(stringBuilder, "exeTime", prefix, tail);
        this.logContentAppend(stringBuilder, "traceTime", prefix, tail);
        this.logContentAppend(stringBuilder, "reserveTm", prefix, tail);
        this.logContentAppend(stringBuilder, "nsAccSttlDt", prefix, tail);
        this.logContentAppend(stringBuilder, "nsSttlDt", prefix, tail);
        this.logContentAppend(stringBuilder, "postingSts", prefix, tail);
        this.logContentAppend(stringBuilder, "cardTp", prefix, tail);
        this.logContentAppend(stringBuilder, "dbtrIssrCd", prefix, tail);
        this.logContentAppend(stringBuilder, "dbtrIdTp", prefix, tail);
        this.logContentAppend(stringBuilder, "dbtrIdNumber", prefix, tail);
        this.logContentAppend(stringBuilder, "dbtrAcctId", prefix, tail);
        this.logContentAppend(stringBuilder, "dbtrNm", prefix, tail);
        this.logContentAppend(stringBuilder, "dbtrContactno", prefix, tail);
        this.logContentAppend(stringBuilder, "cdtrIssrCd", prefix, tail);
        this.logContentAppend(stringBuilder, "cdtrIdTp", prefix, tail);
        this.logContentAppend(stringBuilder, "cdtrIdNumber", prefix, tail);
        this.logContentAppend(stringBuilder, "cdtrAcctId", prefix, tail);
        this.logContentAppend(stringBuilder, "cdtrNm", prefix, tail);
        this.logContentAppend(stringBuilder, "cdtrContactno", prefix, tail);
        this.logContentAppend(stringBuilder, "channleMsg", prefix, tail);
        this.logContentAppend(stringBuilder, "noticeMsg", prefix, tail);
        this.logContentAppend(stringBuilder, "versionNo", prefix, tail);
        this.logContentAppend(stringBuilder, "exd4", prefix, tail);
        this.logContentAppend(stringBuilder, "exd5", prefix, tail);
        this.logContentAppend(stringBuilder, "exd6", prefix, tail);
        this.logContentAppend(stringBuilder, "exd7", prefix, tail);
        this.logContentAppend(stringBuilder, "exd8", prefix, tail);
        this.logContentAppend(stringBuilder, "exd9", prefix, tail);
        return stringBuilder.toString();
    }

    private void logContentAppend(StringBuilder stringBuilder, String logKey, String prefix, String tail) {
        stringBuilder.append("||");
        String appendString = MDC.get(logKey);
        stringBuilder.append(prefix).append(appendString == null ? "" : appendString).append(tail);
    }

    private void logContentStart(StringBuilder stringBuilder, String logKey, String prefix, String tail) {
        String appendString = MDC.get(logKey);
        stringBuilder.append(prefix).append(appendString == null ? "" : appendString).append(tail);
    }
}
