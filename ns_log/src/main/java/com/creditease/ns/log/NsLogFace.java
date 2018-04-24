package com.creditease.ns.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NsLogFace implements NsLogInf {
    private Logger _log = null;

    public NsLogFace() {
    }

    public void setName(String clz) {
        this._log = LoggerFactory.getLogger(clz);
    }

    public boolean isDebugEnabled() {
        return this._log.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return this._log.isInfoEnabled();
    }

    public boolean isWarnEnabled() {
        return this._log.isWarnEnabled();
    }

    public boolean isErrorEnabled() {
        return this._log.isErrorEnabled();
    }

    public boolean isTraceEnabled() {
        return this._log.isTraceEnabled();
    }

    public void trace(String message, Object... args) {
        if (this.isTraceEnabled()) {
            this._log.trace(message, args);
        }

    }

    public void debug(String message, Object... args) {
        if (this.isDebugEnabled()) {
            this._log.debug(message, args);
        }

    }

    public void info(String message, Object... args) {
        if (this.isInfoEnabled()) {
            this._log.info(message, args);
        }

    }

    public void warn(String message, Object... args) {
        if (this.isWarnEnabled()) {
            this._log.warn(message, args);
        }

    }

    public void error(String message, Object... args) {
        if (this.isErrorEnabled()) {
            this._log.error(message, args);
        }

    }

    public void error(Throwable e, String message, Object... args) {
        if (this.isErrorEnabled()) {
            this._log.error(message, args);
        }

    }

    public void error(Throwable e, String message) {
        this._log.error(message + e.toString(), e);
    }

    public void error(String message, Throwable e) {
        this._log.error(message + e.toString(), e);
    }
}
