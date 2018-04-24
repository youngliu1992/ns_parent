//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.creditease.framework.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class ExceptionUtil {
    public ExceptionUtil() {
    }

    public static String getStackTrace(Throwable e) {
        String stackTrace = "";
        Writer writer = null;
        PrintWriter printWriter = null;

        try {
            writer = new StringWriter();
            printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            stackTrace = writer.toString();
        } catch (Exception var17) {
            var17.printStackTrace();
        } finally {
            if (printWriter != null) {
                try {
                    printWriter.close();
                } catch (Exception var16) {
                    var16.printStackTrace();
                }
            }

            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception var15) {
                    var15.printStackTrace();
                }
            }

        }

        return stackTrace;
    }
}
