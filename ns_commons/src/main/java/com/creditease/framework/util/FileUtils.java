//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.creditease.framework.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    public FileUtils() {
    }

    public static void saveAs(String srcfile, String dstfile, boolean append) throws IOException {
        try {
            byte[] bytes = new byte[1048576];
            int size = 0;
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(srcfile));
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dstfile, append));
            boolean var7 = false;

            int n;
            while((n = in.read(bytes)) != -1) {
                size += n;
                out.write(bytes, 0, n);
                out.flush();
            }

            in.close();
            out.close();
            File file = new File(dstfile);
            long dstLen = 0L;
            if (file.isFile()) {
                dstLen = file.length();
            }

            System.out.println("[trace_saveas] [" + srcfile + "] [len:" + size + "] [" + dstfile + "] [dstLen:" + dstLen + "]");
        } catch (Exception var11) {
            var11.printStackTrace();
        }

    }

    public static void saveAsNIO(String srcfile, String dstfile) {
        try {
            FileInputStream fileInputStream = new FileInputStream(srcfile);
            FileOutputStream fileOutputStream = new FileOutputStream(dstfile);
            FileChannel inChannel = fileInputStream.getChannel();
            FileChannel outChannel = fileOutputStream.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int var7 = 0;

            while(true) {
                int eof = inChannel.read(byteBuffer);
                if (eof == -1) {
                    inChannel.close();
                    outChannel.close();
                    File file = new File(dstfile);
                    long dstLen = 0L;
                    if (file.isFile()) {
                        dstLen = file.length();
                    }
                    break;
                }

                byteBuffer.flip();
                outChannel.write(byteBuffer);
                byteBuffer.clear();
                var7 += eof;
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }

    }

    public static String save(InputStream input, String dstfile) throws IOException {
        try {
            byte[] bytes = new byte[1024];
            int size = 0;
            String encodeFileName = null;

            try {
                encodeFileName = new String(dstfile.getBytes("gb2312"), "iso-8859-1");
            } catch (Exception var8) {
                var8.printStackTrace();
            }

            BufferedInputStream in = new BufferedInputStream(input);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(encodeFileName, false));
            boolean var7 = false;

            int n;
            while((n = in.read(bytes)) != -1) {
                size += n;
                out.write(bytes, 0, n);
                out.flush();
            }

            in.close();
            out.flush();
            out.close();
            return dstfile;
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    public static String getContent(InputStream input) throws IOException {
        try {
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean var3 = false;

            int n;
            while((n = input.read(bytes)) != -1) {
                out.write(bytes, 0, n);
                out.flush();
            }

            input.close();
            out.close();
            return out.toString();
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static void chkFolder(String file) {
        String[] str = file.split("/");
        String path = "";
        if (file.indexOf(":") != -1) {
            path = file.substring(0, 2);
        }

        for(int i = 1; i < str.length; ++i) {
            if (str[i].indexOf(".") == -1 || file.substring((path + "/" + str[i]).length()).indexOf("/") != -1) {
                path = path + "/" + str[i];
                File nowfile = new File(path);
                if (!nowfile.isDirectory()) {
                    nowfile.mkdir();
                }
            }
        }

    }

    public static String readFile(String file) {
        try {
            byte[] data = readFileData(file);
            return new String(data);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static byte[] readFileData(String file) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        try {
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[512];
            boolean var4 = false;

            int n;
            while((n = in.read(buffer)) != -1) {
                bout.write(buffer, 0, n);
            }

            in.close();
            byte[] data = bout.toByteArray();
            bout.close();
            return data;
        } catch (Exception var6) {
            var6.printStackTrace();
            return new byte[0];
        }
    }

    public static byte[] readFileData2(String file) {
        ArrayList blist = new ArrayList();

        try {
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[512];
            boolean var4 = false;

            int n;
            while((n = in.read(buffer)) != -1) {
                for(int i = 0; i < n; ++i) {
                    blist.add(new Byte(buffer[i]));
                }
            }

            in.close();
            byte[] data = new byte[blist.size()];

            for(int i = 0; i < data.length; ++i) {
                data[i] = ((Byte)blist.get(i)).byteValue();
            }

            return data;
        } catch (Exception var7) {
            var7.printStackTrace();
            return new byte[0];
        }
    }

    public static void writeFile(String content, String file) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(content);
            out.close();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public static void writeFile(String content, String file, boolean append) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file, append));
            out.write(content);
            out.close();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public static void writeFile(String file, byte[] data) {
        chkFolder(file);

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(data);
            out.flush();
            out.close();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public static void saveUrl(String url, String dstFile) {
        try {
            URL ourl = new URL(url);
            URLConnection con = ourl.openConnection();
            InputStream in = con.getInputStream();
            byte[] buffer = new byte[512];
            int n = false;
            int total = 0;
            FileOutputStream fout = new FileOutputStream(dstFile);

            int n;
            while((n = in.read(buffer)) != -1) {
                total += n;
                fout.write(buffer, 0, n);
            }

            in.close();
            fout.close();
            System.out.println("download complete.[" + total + " bytes]");
        } catch (Exception var9) {
            var9.printStackTrace();
        }

    }

    public static void copy(File srcFile, File dstFile, boolean overwrite) throws Exception {
        if (srcFile.isDirectory()) {
            chkFolder(dstFile.getPath());
            File[] files = srcFile.listFiles();
            File[] arr$ = files;
            int len$ = files.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                File f = arr$[i$];
                String subSrcF = f.getPath().substring(srcFile.getPath().length());
                String dstF = dstFile.getPath() + subSrcF;
                copy(f, new File(dstF), overwrite);
            }
        } else {
            saveAs(srcFile.getPath(), dstFile.getPath(), !overwrite);
        }

    }

    public static void remove(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                File[] arr$ = files;
                int len$ = files.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    File f = arr$[i$];
                    remove(f);
                }
            }
        }

        file.delete();
    }

    public static void clear(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                File[] arr$ = files;
                int len$ = files.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    File f = arr$[i$];
                    remove(f);
                }
            }
        }

    }

    public static void main(String[] args) {
    }

    public static void removePrefixFolder(File folder, String prefix) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            File[] arr$ = files;
            int len$ = files.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                File f = arr$[i$];
                if (f.getName().startsWith(prefix)) {
                    removeFolder(f);
                }
            }
        }

    }

    public static void removeFolder(File folder) {
        File[] files = folder.listFiles();
        File[] arr$ = files;
        int len$ = files.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            File f = arr$[i$];
            f.delete();
        }

        folder.delete();
    }

    public static String findFile(String name, String dstFolder) {
        File dst = new File(dstFolder);
        String matched = null;
        if (dst.isDirectory()) {
            File[] fs = dst.listFiles();
            File[] arr$ = fs;
            int len$ = fs.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                File f = arr$[i$];
                String n;
                if (f.isFile()) {
                    n = f.getName();
                    n = n.substring(0, n.lastIndexOf("."));
                    if (name.equals(n)) {
                        matched = f.getPath();
                        break;
                    }
                } else {
                    n = findFile(name, f.getPath());
                    if (n != null) {
                        matched = n;
                        break;
                    }
                }
            }
        }

        return matched;
    }

    public static List<String> listAllFiles(String path) {
        List<String> list = new ArrayList();
        File f = new File(path);
        if (f.isDirectory()) {
            File[] fs = f.listFiles();
            File[] arr$ = fs;
            int len$ = fs.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                File file = arr$[i$];
                collectFiles(file.getPath(), list);
            }
        }

        return list;
    }

    public static void collectFiles(String path, List<String> collection) {
        File f = new File(path);
        if (f.isDirectory()) {
            File[] fs = f.listFiles();
            File[] arr$ = fs;
            int len$ = fs.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                File file = arr$[i$];
                collectFiles(file.getPath(), collection);
            }
        } else {
            collection.add(path);
        }

    }

    public static double getDiskUsage(String diskFilePath) {
        File f = new File(diskFilePath);
        if (f.exists()) {
            long total = f.getTotalSpace();
            if (total > 0L) {
                long free = f.getFreeSpace();
                long used = total - free;
                used = used < 0L ? 0L : used;
                double usage = (new Double((double)used)).doubleValue() / (new Double((double)total)).doubleValue();
                return usage;
            }
        }

        return 0.0D;
    }

    public static String convertToAbsolutePath(String resoucePath) {
        String path = resoucePath;
        if (resoucePath.startsWith("/")) {
            path = resoucePath.substring(1);
        }

        return getDefaultClassLoader().getResource(path).getPath();
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;

        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable var2) {
            ;
        }

        if (cl == null) {
            cl = FileUtils.class.getClassLoader();
        }

        return cl;
    }
}
