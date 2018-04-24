package com.creditease.ns.log.util;

import com.creditease.ns.log.NsLog;
import java.util.Date;
import java.util.List;

public class PrintUtil {
    public PrintUtil() {
    }

    public static void printNs4() {
        System.out.println("=========================================================");
        System.out.println("");
        System.out.println("\t*       *     * * *          *    ");
        System.out.println("\t* *     *    *     *\t    **    ");
        System.out.println("\t*  *    *    * \t           * *    ");
        System.out.println("\t*   *   *      *          *  *    ");
        System.out.println("\t*    *  *         *      *   *    ");
        System.out.println("\t*     * *          *    * * * * * ");
        System.out.println("\t*      **    *     *         *    ");
        System.out.println("\t*       *     * * *          *    ");
        System.out.println();
    }

    public static void printJVM() {
        System.out.println("======== JVM INFO [" + new Date() + "] ========");
        System.out.println("[Classpath]:");
        String[] paths = Env.getClassPath();

        for(int i = 0; i < paths.length; ++i) {
            System.out.println(paths[i]);
        }

        System.out.println("");
        System.out.println("[Other]:");
        System.out.print("Java Runtime Environment version               :");
        System.out.println(System.getProperty("java.version"));
        System.out.print("Java Runtime Environment vendor                :");
        System.out.println(System.getProperty("java.vendor"));
        System.out.print("Java vendor URL                                :");
        System.out.println(System.getProperty("java.vendor.url"));
        System.out.print("Java installation directory                    :");
        System.out.println(System.getProperty("java.home"));
        System.out.print("Java Virtual Machine specification version     :");
        System.out.println(System.getProperty("java.vm.specification.version"));
        System.out.print("Java Virtual Machine specification vendor      :");
        System.out.println(System.getProperty("java.vm.specification.vendor"));
        System.out.print("Java Virtual Machine specification name        :");
        System.out.println(System.getProperty("java.vm.specification.name"));
        System.out.print("Java Virtual Machine implementation version    :");
        System.out.println(System.getProperty("java.vm.version"));
        System.out.print("Java Virtual Machine implementation vendor     :");
        System.out.println(System.getProperty("java.vm.vendor"));
        System.out.print("Java Virtual Machine implementation name       :");
        System.out.println(System.getProperty("java.vm.name"));
        System.out.print("Java Runtime Environment specification version :");
        System.out.println(System.getProperty("java.specification.version"));
        System.out.print("Java Runtime Environment specification vendor  :");
        System.out.println(System.getProperty("java.specification.vendor"));
        System.out.print("Java Runtime Environment specification name    :");
        System.out.println(System.getProperty("java.specification.name"));
        System.out.print("Java class format version number               :");
        System.out.println(System.getProperty("java.class.version"));
        System.out.print("List of paths to search when loading libraries :");
        System.out.println(System.getProperty("java.library.path"));
        System.out.print("Default temp file path                         :");
        System.out.println(System.getProperty("java.io.tmpdir"));
        System.out.print("Name of JIT compiler to use                    :");
        System.out.println(System.getProperty("java.compiler"));
        System.out.print("Path of extension directory or directories     :");
        System.out.println(System.getProperty("java.ext.dirs"));
        System.out.print("Operating system name                          :");
        System.out.println(System.getProperty("os.name"));
        System.out.print("Operating system architecture                  :");
        System.out.println(System.getProperty("os.arch"));
        System.out.print("Operating system version                       :");
        System.out.println(System.getProperty("os.version"));
        System.out.print("File separator                                 :");
        System.out.println(System.getProperty("file.separator"));
        System.out.print("Path separator                                 :");
        System.out.println(System.getProperty("path.separator"));
        System.out.print("Line separator                                 :");
        System.out.println(System.getProperty("line.separator"));
        System.out.print("User's account name                            :");
        System.out.println(System.getProperty("user.name"));
        System.out.print("User's home directory                          :");
        System.out.println(System.getProperty("user.home"));
        System.out.print("User's current working directory               :");
        System.out.println(System.getProperty("user.dir"));
        System.out.println("===========================================");
    }

    public static void printDateStamp(NsLog log, String mes) {
        if (log != null) {
            log.info("┌                                        ┐", new Object[0]);
            log.info("  Time:    " + new Date(), new Object[0]);
            log.info("  Message: " + mes, new Object[0]);
            log.info("└                                        ┘", new Object[0]);
        } else {
            System.out.println("┌                                        ┐");
            System.out.println("  Time:    " + new Date());
            System.out.println("  Message: " + mes);
            System.out.println("└                                        ┘");
        }

    }

    public static void printDateStamp(NsLog log, String[] mes) {
        int i;
        if (log != null) {
            log.info("┌                                        ┐", new Object[0]);
            log.info("  Time:    " + new Date(), new Object[0]);

            for(i = 0; i < mes.length; ++i) {
                log.info("  " + i + ": " + mes[i], new Object[0]);
            }

            log.info("└                                        ┘", new Object[0]);
        } else {
            System.out.println("┌                                        ┐");
            System.out.println("  Time:    " + new Date());

            for(i = 0; i < mes.length; ++i) {
                System.out.println("  " + i + ": " + mes[i]);
            }

            System.out.println("└                                        ┘");
        }

    }

    public static void printDateStamp(NsLog log, List<String> mes) {
        int i;
        if (log != null) {
            log.info("┌                                                ┐", new Object[0]);
            log.info("  Time:    " + new Date(), new Object[0]);

            for(i = 0; i < mes.size(); ++i) {
                log.info("  " + i + ": " + (String)mes.get(i), new Object[0]);
            }

            log.info("└                                                ┘", new Object[0]);
        } else {
            System.out.println("┌                                                ┐");
            System.out.println("  Time:    " + new Date());

            for(i = 0; i < mes.size(); ++i) {
                System.out.println("  " + i + ": " + (String)mes.get(i));
            }

            System.out.println("└                                                ┘");
        }

    }
}
