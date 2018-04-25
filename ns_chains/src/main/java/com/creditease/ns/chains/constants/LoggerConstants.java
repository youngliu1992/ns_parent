package com.creditease.ns.chains.constants;

import com.creditease.ns.log.spi.LoggerWrapper;
import org.slf4j.LoggerFactory;

public class LoggerConstants {
    public static LoggerWrapper DEF_LOGGER = LoggerWrapper.getLoggerWrapper(LoggerFactory.getLogger("chains.def"));
    public static LoggerWrapper CHAIN_LOGGER = LoggerWrapper.getLoggerWrapper(LoggerFactory.getLogger("chains.chain"));
    public static LoggerWrapper EXCHANGE_LOGGER = LoggerWrapper.getLoggerWrapper(LoggerFactory.getLogger("chains.exchange"));
    public static LoggerWrapper CHAINS_LOGGER = LoggerWrapper.getLoggerWrapper(LoggerFactory.getLogger("chains.system"));
    public static LoggerWrapper COMMAND_LOGGER = LoggerWrapper.getLoggerWrapper(LoggerFactory.getLogger("chains.command"));

    public LoggerConstants() {
    }
}