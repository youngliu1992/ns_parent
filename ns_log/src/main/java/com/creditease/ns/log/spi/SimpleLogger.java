package com.creditease.ns.log.spi;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class SimpleLogger {
    private static Logger logger = LoggerFactory.getLogger(SimpleLogger.class);
    private static SimpleLogger simpleLogger = new SimpleLogger();

    public static SimpleLogger getLoggerSetting() {
        return simpleLogger;
    }

    private SimpleLogger() {
    }

    public static void log(String modulName, String msg) {
        logger.info("{}||{}", modulName, msg);
    }

    public SimpleLogger setTxnId(String txnId) {
        MDC.put("txnId", txnId);
        return this;
    }

    public SimpleLogger setOrderId(String orderId) {
        MDC.put("orderId", orderId);
        return this;
    }

    public SimpleLogger setSystemOrderId(String systemOrderId) {
        MDC.put("systemOrderId", systemOrderId);
        return this;
    }

    public SimpleLogger setBatchId(String batchId) {
        MDC.put("batchId", batchId);
        return this;
    }

    public SimpleLogger setTradeStatus(String tradeStatus) {
        MDC.put("tradeStatus", tradeStatus);
        return this;
    }

    public SimpleLogger setResponseCodeMess(String responseCodeMess) {
        MDC.put("responseCodeMess", responseCodeMess);
        return this;
    }

    public SimpleLogger setResponseCode(String responseCode) {
        MDC.put("responseCode", responseCode);
        return this;
    }

    public SimpleLogger setMerchantId(String merchantId) {
        MDC.put("merchantId", merchantId);
        return this;
    }

    public SimpleLogger setChannelMerchantId(String channelMerchantId) {
        MDC.put("channelMerchantId", channelMerchantId);
        return this;
    }

    public SimpleLogger setChannelName(String channelName) {
        MDC.put("channelName", channelName);
        return this;
    }

    public SimpleLogger setPayType(String payType) {
        MDC.put("payType", payType);
        return this;
    }

    public SimpleLogger setTxnType(String txnType) {
        MDC.put("txnType", txnType);
        return this;
    }

    public SimpleLogger setUpdateTime(String updateTime) {
        MDC.put("updateTime", updateTime);
        return this;
    }

    public SimpleLogger setRemark(String remark) {
        MDC.put("remark", remark);
        return this;
    }

    public SimpleLogger setErrorDesp(String errorDesp) {
        MDC.put("errorDesp", errorDesp);
        return this;
    }

    public SimpleLogger setChkStatus(String chkSts) {
        MDC.put("chkSts", chkSts);
        return this;
    }

    public SimpleLogger setOperatorId(String operatorId) {
        MDC.put("operatorId", operatorId);
        return this;
    }

    public SimpleLogger setThirdHeadCode(String thirdHeadCode) {
        MDC.put("thirdHeadCode", thirdHeadCode);
        return this;
    }

    public SimpleLogger setThirdHeadMsg(String thirdHeadMsg) {
        MDC.put("thirdHeadMsg", thirdHeadMsg);
        return this;
    }

    public SimpleLogger setThirdBodyMsg(String thirdBodyMsg) {
        MDC.put("thirdBodyMsg", thirdBodyMsg);
        return this;
    }

    public SimpleLogger setThirdBodyCode(String thirdBodyCode) {
        MDC.put("thirdBodyCode", thirdBodyCode);
        return this;
    }

    public SimpleLogger setchanId(String chanId) {
        MDC.put("chanId", chanId);
        return this;
    }

    public SimpleLogger setSttlAmt(String sttlAmt) {
        MDC.put("sttlAmt", sttlAmt);
        return this;
    }

    public SimpleLogger setActualSttlAmt(String actualSttlAmt) {
        MDC.put("actualSttlAmt", actualSttlAmt);
        return this;
    }

    public SimpleLogger setSplitFlg(String splitFlg) {
        MDC.put("splitFlg", splitFlg);
        return this;
    }

    public SimpleLogger setProcSts(String procSts) {
        MDC.put("procSts", procSts);
        return this;
    }

    public SimpleLogger setNotifyUrl(String notifyUrl) {
        MDC.put("notifyUrl", notifyUrl);
        return this;
    }

    public SimpleLogger setOutTxnId(String outTxnId) {
        MDC.put("outTxnId", outTxnId);
        return this;
    }

    public SimpleLogger setExTxnTd(String exTxnTd) {
        MDC.put("exTxnTd", exTxnTd);
        return this;
    }

    public SimpleLogger setCaptureTime(String captureTime) {
        MDC.put("captureTime", captureTime);
        return this;
    }

    public SimpleLogger setExeTime(String exeTime) {
        MDC.put("exeTime", exeTime);
        return this;
    }

    public SimpleLogger setTraceTime(String traceTime) {
        MDC.put("traceTime", traceTime);
        return this;
    }

    public SimpleLogger setReserveTm(String reserveTm) {
        MDC.put("reserveTm", reserveTm);
        return this;
    }

    public SimpleLogger setNsAccSttlDt(String nsAccSttlDt) {
        MDC.put("nsAccSttlDt", nsAccSttlDt);
        return this;
    }

    public SimpleLogger setNsSttlDt(String nsSttlDt) {
        MDC.put("nsSttlDt", nsSttlDt);
        return this;
    }

    public SimpleLogger setPostingSts(String postingSts) {
        MDC.put("postingSts", postingSts);
        return this;
    }

    public SimpleLogger setCardTp(String cardTp) {
        MDC.put("cardTp", cardTp);
        return this;
    }

    public SimpleLogger setDbtrIssrCd(String dbtrIssrCd) {
        MDC.put("dbtrIssrCd", dbtrIssrCd);
        return this;
    }

    public SimpleLogger setDbtrIdTp(String dbtrIdTp) {
        MDC.put("dbtrIdTp", dbtrIdTp);
        return this;
    }

    public SimpleLogger setDbtrIdNumber(String dbtrIdNumber) {
        MDC.put("dbtrIdNumber", dbtrIdNumber);
        return this;
    }

    public SimpleLogger setDbtrAcctId(String dbtrAcctId) {
        MDC.put("dbtrAcctId", dbtrAcctId);
        return this;
    }

    public SimpleLogger setDbtrNm(String dbtrNm) {
        MDC.put("dbtrNm", dbtrNm);
        return this;
    }

    public SimpleLogger setDbtrContactno(String dbtrContactno) {
        MDC.put("dbtrContactno", dbtrContactno);
        return this;
    }

    public SimpleLogger setCdtrIssrCd(String cdtrIssrCd) {
        MDC.put("cdtrIssrCd", cdtrIssrCd);
        return this;
    }

    public SimpleLogger setCdtrIdTp(String cdtrIdTp) {
        MDC.put("cdtrIdTp", cdtrIdTp);
        return this;
    }

    public SimpleLogger setCdtrIdNumber(String cdtrIdNumber) {
        MDC.put("cdtrIdNumber", cdtrIdNumber);
        return this;
    }

    public SimpleLogger setCdtrAcctId(String cdtrAcctId) {
        MDC.put("cdtrAcctId", cdtrAcctId);
        return this;
    }

    public SimpleLogger setCdtrNm(String cdtrNm) {
        MDC.put("cdtrNm", cdtrNm);
        return this;
    }

    public SimpleLogger setCdtrContactno(String cdtrContactno) {
        MDC.put("cdtrContactno", cdtrContactno);
        return this;
    }

    public SimpleLogger setChannleMsg(String channleMsg) {
        MDC.put("channleMsg", channleMsg);
        return this;
    }

    public SimpleLogger setNoticeMsg(String noticeMsg) {
        MDC.put("noticeMsg", noticeMsg);
        return this;
    }

    public SimpleLogger setVersionNo(String versionNo) {
        MDC.put("versionNo", versionNo);
        return this;
    }

    public SimpleLogger setExd4(String exd4) {
        MDC.put("exd4", exd4);
        return this;
    }

    public SimpleLogger setExd5(String exd5) {
        MDC.put("exd5", exd5);
        return this;
    }

    public SimpleLogger setExd6(String exd6) {
        MDC.put("exd6", exd6);
        return this;
    }

    public SimpleLogger setExd7(String exd7) {
        MDC.put("exd7", exd7);
        return this;
    }

    public SimpleLogger setExd8(String exd8) {
        MDC.put("exd8", exd8);
        return this;
    }

    public SimpleLogger setExd9(String exd9) {
        MDC.put("exd9", exd9);
        return this;
    }

    public static void reset() {
        MDC.setContextMap(new HashMap());
    }
}
