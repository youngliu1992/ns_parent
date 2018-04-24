package com.creditease.ns.log.converter;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import com.creditease.ns.log.LogKey;

public class NSThrowableConvert extends ThrowableProxyConverter {
    public NSThrowableConvert() {
    }

    protected String throwableProxyToString(IThrowableProxy tp) {
        StringBuilder sb = new StringBuilder(2048);
        this.recursiveAppend(sb, (String)null, 1, tp);
        return sb.toString();
    }

    private void recursiveAppend(StringBuilder sb, String prefix, int indent, IThrowableProxy tp) {
        if (tp != null) {
            this.subjoinFirstLine(sb, prefix, indent, tp);
            sb.append(CoreConstants.LINE_SEPARATOR);
            this.subjoinSTEPArray(sb, indent, tp);
            IThrowableProxy[] suppressed = tp.getSuppressed();
            if (suppressed != null) {
                IThrowableProxy[] arr$ = suppressed;
                int len$ = suppressed.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    IThrowableProxy current = arr$[i$];
                    this.recursiveAppend(sb, "Suppressed: ", indent + 1, current);
                }
            }

            this.recursiveAppend(sb, "Caused by: ", indent, tp.getCause());
        }
    }

    private void subjoinFirstLine(StringBuilder buf, String prefix, int indent, IThrowableProxy tp) {
        ThrowableProxyUtil.indent(buf, indent - 1);
        buf.append(" - ");
        if (prefix != null) {
            buf.append(prefix);
        }

        this.subjoinExceptionMessage(buf, tp);
    }

    private void subjoinExceptionMessage(StringBuilder buf, IThrowableProxy tp) {
        buf.append(tp.getClassName()).append(": ").append(tp.getMessage());
    }

    public void subjoinSTEPArray(StringBuilder buf, int indent, IThrowableProxy tp) {
        String totalKey = LogKey.getTotalKey();
        StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
        int commonFrames = tp.getCommonFrames();
        boolean unrestrictedPrinting = 2147483647 > stepArray.length;
        int maxIndex = unrestrictedPrinting ? stepArray.length : 2147483647;
        if (commonFrames > 0 && unrestrictedPrinting) {
            maxIndex -= commonFrames;
        }

        for(int i = 0; i < maxIndex; ++i) {
            buf.append(totalKey);
            ThrowableProxyUtil.indent(buf, indent);
            buf.append(stepArray[i]);
            this.extraData(buf, stepArray[i]);
            buf.append(CoreConstants.LINE_SEPARATOR);
        }

        if (commonFrames > 0 && unrestrictedPrinting) {
            buf.append(totalKey);
            ThrowableProxyUtil.indent(buf, indent);
            buf.append("... ").append(tp.getCommonFrames()).append(" common frames omitted").append(CoreConstants.LINE_SEPARATOR);
        }

    }
}
