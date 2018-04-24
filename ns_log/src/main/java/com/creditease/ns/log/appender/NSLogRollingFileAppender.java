package com.creditease.ns.log.appender;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.RollingPolicyBase;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class NSLogRollingFileAppender<E> extends FileAppender<E> {
    File currentlyActiveFile;
    TriggeringPolicy<E> triggeringPolicy;
    RollingPolicy rollingPolicy;
    private static String RFA_NO_TP_URL = "http://logback.qos.ch/codes.html#rfa_no_tp";
    private static String RFA_NO_RP_URL = "http://logback.qos.ch/codes.html#rfa_no_rp";
    private static String COLLISION_URL = "http://logback.qos.ch/codes.html#rfa_collision";

    public NSLogRollingFileAppender() {
    }

    public void start() {
        if (this.triggeringPolicy == null) {
            this.addWarn("No TriggeringPolicy was set for the RollingFileAppender named " + this.getName());
            this.addWarn("For more information, please visit " + RFA_NO_TP_URL);
        } else {
            if (!this.append) {
                this.addWarn("Append mode is mandatory for RollingFileAppender");
                this.append = true;
            }

            if (this.rollingPolicy == null) {
                this.addError("No RollingPolicy was set for the RollingFileAppender named " + this.getName());
                this.addError("For more information, please visit " + RFA_NO_RP_URL);
            } else if (this.fileAndPatternCollide()) {
                this.addError("File property collides with fileNamePattern. Aborting.");
                this.addError("For more information, please visit " + COLLISION_URL);
            } else {
                if (this.isPrudent()) {
                    if (this.rawFileProperty() != null) {
                        this.addWarn("Setting \"File\" property to null on account of prudent mode");
                        this.setFile((String)null);
                    }

                    if (this.rollingPolicy.getCompressionMode() != CompressionMode.NONE) {
                        this.addError("Compression is not supported in prudent mode. Aborting");
                        return;
                    }
                }

                this.currentlyActiveFile = new File(this.getFile());
                this.addInfo("Active log file name: " + this.getFile());
            }
        }
    }

    public void realStart() {
        this.start();
        super.start();
    }

    private boolean fileAndPatternCollide() {
        if (this.triggeringPolicy instanceof RollingPolicyBase) {
            RollingPolicyBase base = (RollingPolicyBase)this.triggeringPolicy;
            FileNamePattern fileNamePattern = null;

            try {
                Field fileNamePatternField = base.getClass().getSuperclass().getDeclaredField("fileNamePattern");
                fileNamePatternField.setAccessible(true);
                fileNamePattern = (FileNamePattern)fileNamePatternField.get(base);
            } catch (Exception var5) {
                var5.printStackTrace();
            }

            if (fileNamePattern != null && this.fileName != null) {
                String regex = fileNamePattern.toRegex();
                return this.fileName.matches(regex);
            }
        }

        return false;
    }

    public void stop() {
        if (this.rollingPolicy != null) {
            this.rollingPolicy.stop();
        }

        if (this.triggeringPolicy != null) {
            this.triggeringPolicy.stop();
        }

        super.stop();
    }

    public void setFile(String file) {
        if (file != null && (this.triggeringPolicy != null || this.rollingPolicy != null)) {
            this.addError("File property must be set before any triggeringPolicy or rollingPolicy properties");
            this.addError("Visit http://logback.qos.ch/codes.html#rfa_file_after for more information");
        }

        super.setFile(file);
    }

    public String getFile() {
        return this.rollingPolicy.getActiveFileName();
    }

    public void rollover() {
        this.lock.lock();

        try {
            this.closeOutputStream();
            this.attemptRollover();
            this.attemptOpenFile();
        } finally {
            this.lock.unlock();
        }

    }

    private void attemptOpenFile() {
        try {
            this.currentlyActiveFile = new File(this.rollingPolicy.getActiveFileName());
            this.openFile(this.rollingPolicy.getActiveFileName());
        } catch (IOException var2) {
            this.addError("setFile(" + this.fileName + ", false) call failed.", var2);
        }

    }

    private void attemptRollover() {
        try {
            this.rollingPolicy.rollover();
        } catch (RolloverFailure var2) {
            this.addWarn("RolloverFailure occurred. Deferring roll-over.");
            this.append = true;
        }

    }

    protected void subAppend(E event) {
        TriggeringPolicy var2 = this.triggeringPolicy;
        synchronized(this.triggeringPolicy) {
            if (this.triggeringPolicy.isTriggeringEvent(this.currentlyActiveFile, event)) {
                this.rollover();
            }
        }

        super.subAppend(event);
    }

    public RollingPolicy getRollingPolicy() {
        return this.rollingPolicy;
    }

    public TriggeringPolicy<E> getTriggeringPolicy() {
        return this.triggeringPolicy;
    }

    public void setRollingPolicy(RollingPolicy policy) {
        this.rollingPolicy = policy;
        if (this.rollingPolicy instanceof TriggeringPolicy) {
            this.triggeringPolicy = (TriggeringPolicy)policy;
        }

    }

    public void setTriggeringPolicy(TriggeringPolicy<E> policy) {
        this.triggeringPolicy = policy;
        if (policy instanceof RollingPolicy) {
            this.rollingPolicy = (RollingPolicy)policy;
        }

    }
}
