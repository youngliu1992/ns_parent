package com.creditease.ns.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Log {
    private static Logger processLog = LoggerFactory.getLogger("processLog");
    private static Logger errorLog = LoggerFactory.getLogger("errorLog");
    private static Logger infoLog = LoggerFactory.getLogger("infoLog");
    private static Logger debugLog = LoggerFactory.getLogger("debugLog");

    public Log() {
    }

    public static void setPrimayKey(String primayKey) {
        MDC.put("primaryKey", primayKey);
    }

    public static void setSubPrimary(String subPrimaryKey) {
        MDC.put("subprimaryKey", subPrimaryKey);
    }

    public static void setUniqKey() {
        MDC.put("uuid", LogUtils.getShortUUID());
    }

    public static void setModuleName(String moduleName) {
        MDC.put("moduleName", moduleName);
    }

    public static void logProcess(String processName, String msg) {
        processLog.info("[{}],msg:{}", processName, msg);
    }

    public static void logProcess(String processName, LogCode logCode, String msg) {
        processLog.info("[{}],状态码:{},状态信息:{},msg:{}", logCode.getCode(), logCode.getMsg());
    }

    public static void logProcess(String processName) {
        processLog.info("[{}]", processName);
    }

    public static void logErrorToProcessFile(String msg, Throwable throwable) {
        processLog.error(msg, throwable);
    }

    public static void logErrorToProcessFile(String msg) {
        processLog.error(msg);
    }

    public static void logErrorToProcessFile(LogCode logCode) {
        processLog.error("错误码:{},错误信息:{}", logCode.getCode(), logCode.getMsg());
    }

    public static void logErrorToProcessFile(LogCode logCode, String msg) {
        processLog.error("错误码:{},错误信息:{},附加信息:{}", new Object[]{logCode.getCode(), logCode.getMsg(), msg});
    }

    public static void logErrorToProcessFile(LogCode logCode, String msg, Throwable throwable) {
        processLog.error("错误码:{},错误信息:{},附加信息:{}", new Object[]{logCode.getCode(), logCode.getMsg(), msg, throwable});
    }

    public static void logError(String msg, Throwable throwable) {
        processLog.error(msg, throwable);
    }

    public static void logError(String msg) {
        errorLog.error(msg);
    }

    public static void logError(LogCode logCode) {
        errorLog.error("错误码:{},错误信息:{}", logCode.getCode(), logCode.getMsg());
    }

    public static void logError(LogCode logCode, String msg) {
        errorLog.error("错误码:{},错误信息:{},附加信息:{}", new Object[]{logCode.getCode(), logCode.getMsg(), msg});
    }

    public static void logError(LogCode logCode, String msg, Throwable throwable) {
        errorLog.error("错误码:{},错误信息:{},附加信息:{}", new Object[]{logCode.getCode(), logCode.getMsg(), msg, throwable});
    }

    public static void logInformation(String msg) {
        infoLog.info(msg);
    }

    public static void logInformation(String format, Object[] params) {
        infoLog.info(format, params);
    }

    public static void logInfoToProcessFile(String msg) {
        processLog.info(msg);
    }

    public static void logInfoToProcessFile(String format, Object[] params) {
        processLog.info(format, params);
    }

    public static void logDebug(String msg) {
        debugLog.debug(msg);
    }

    public static void logDebug(String format, Object[] params) {
        debugLog.debug(format, params);
    }

    public static void logDebugToProcessFile(String msg) {
        processLog.debug(msg);
    }

    public static void logDebugToProcessFile(String format, Object[] params) {
        processLog.debug(format, params);
    }
}
