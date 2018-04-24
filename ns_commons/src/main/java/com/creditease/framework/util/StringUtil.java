package com.creditease.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String[] PROVINCE_ZH = new String[]{"北京", "天津", "河北", "山西", "内蒙古", "辽宁", "吉林", "黑龙江", "上海", "江苏", "浙江", "安徽", "福建", "江西", "山东", "河南", "湖北", "湖南", "广东", "广西", "海南", "重庆", "四川", "贵州", "云南", "西藏", "陕西", "甘肃", "青海", "宁夏", "新疆"};
    public static String[] PROVINCE_EN = new String[]{"beijing", "tianjin", "hebei", "shanxi", "neimenggu", "liaoning", "jilin", "heilongjiang", "shanghai", "jiangsu", "zhejiang", "anhui", "fujian", "jiangxi", "shandong", "henan", "hubei", "hunan", "guangdong", "guangxi", "hainan", "chongqing", "sichuan", "guizhou", "yunnan", "xizang", "shanxi1", "gansu", "qinghai", "ningxia", "xinjiang"};
    public static int MAX_SDFMAP_SIZE = 1024;
    private static Hashtable sdfMap = new Hashtable();
    private static MessageDigest digest = null;
    private static Random randGen = new Random();
    private static char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static char[] numbers = "0123456789".toCharArray();
    private static final char[] HEXDUMP_TABLE = new char[1024];

    public StringUtil() {
    }

    public static String addcommas(long l) {
        String s = "";
        String number = "" + l;
        char[] chars = number.toCharArray();
        int k = 0;

        for(int i = chars.length - 1; i >= 0; --i) {
            s = chars[i] + s;
            ++k;
            if (k % 3 == 0 && i > 0) {
                s = "," + s;
            }
        }

        return s;
    }

    public static String addcommas(int l) {
        return addcommas((long)l);
    }

    public static String formatDate(Date d, String format) {
        if (format == null || format.trim().length() == 0) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat f = (SimpleDateFormat)sdfMap.get(format);
        if (f == null) {
            try {
                f = new SimpleDateFormat(format);
            } catch (Exception var4) {
                return d.toString();
            }

            sdfMap.put(format, f);
            if (sdfMap.size() > MAX_SDFMAP_SIZE) {
                sdfMap.clear();
            }
        }

        return f.format(d);
    }

    public static Date parseDate(String formatedDate, String format) {
        if (format == null || format.trim().length() == 0) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat f = (SimpleDateFormat)sdfMap.get(format);
        if (f == null) {
            try {
                f = new SimpleDateFormat(format);
            } catch (Exception var5) {
                return null;
            }

            sdfMap.put(format, f);
            if (sdfMap.size() > MAX_SDFMAP_SIZE) {
                sdfMap.clear();
            }
        }

        try {
            return f.parse(formatedDate);
        } catch (Exception var4) {
            return null;
        }
    }

    public static int parseInt(String str, int defaultValue) {
        boolean var2 = false;

        int ret;
        try {
            ret = Integer.parseInt(str);
        } catch (Exception var4) {
            ret = defaultValue;
        }

        return ret;
    }

    public static float parseFloat(String str, float defaultValue) {
        float ret = 0.0F;

        try {
            ret = Float.parseFloat(str);
        } catch (Exception var4) {
            ret = defaultValue;
        }

        return ret;
    }

    public static final String replace(String line, String oldString, String newString) {
        if (line == null) {
            return null;
        } else {
            int i = 0;
            if ((i = line.indexOf(oldString, i)) < 0) {
                return line;
            } else {
                char[] line2 = line.toCharArray();
                char[] newString2 = newString.toCharArray();
                int oLength = oldString.length();
                StringBuffer buf = new StringBuffer(line2.length);
                buf.append(line2, 0, i).append(newString2);
                i += oLength;

                int j;
                for(j = i; (i = line.indexOf(oldString, i)) > 0; j = i) {
                    buf.append(line2, j, i - j).append(newString2);
                    i += oLength;
                }

                buf.append(line2, j, line2.length - j);
                return buf.toString();
            }
        }
    }

    public static final synchronized String hash(String data) {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var2) {
                System.err.println("Failed to load the MD5 MessageDigest. Jive will be unable to function normally.");
                var2.printStackTrace();
            }
        }

        digest.update(data.getBytes());
        return toHex(digest.digest());
    }

    public static final synchronized String hash(byte[] data) {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var2) {
                System.err.println("Failed to load the MD5 MessageDigest. Jive will be unable to function normally.");
                var2.printStackTrace();
            }
        }

        digest.update(data);
        return toHex(digest.digest());
    }

    public static final synchronized String hash(String data, String encoding) throws UnsupportedEncodingException {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var3) {
                System.err.println("Failed to load the MD5 MessageDigest. Jive will be unable to function normally.");
                var3.printStackTrace();
            }
        }

        if (encoding != null && encoding.length() > 0) {
            digest.update(data.getBytes(encoding));
        } else {
            digest.update(data.getBytes());
        }

        return toHex(digest.digest());
    }

    public static String getFileMD5(File file) {
        if (file.exists() && file.isFile()) {
            MessageDigest digest = null;
            FileInputStream in = null;
            byte[] buffer = new byte[1024];

            try {
                digest = MessageDigest.getInstance("MD5");
                in = new FileInputStream(file);

                while(true) {
                    int len;
                    if ((len = in.read(buffer, 0, 1024)) == -1) {
                        in.close();
                        break;
                    }

                    digest.update(buffer, 0, len);
                }
            } catch (Exception var6) {
                var6.printStackTrace();
                return null;
            }

            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } else {
            return null;
        }
    }

    public static final String toHex(byte[] hash) {
        StringBuffer buf = new StringBuffer(hash.length * 2);

        for(int i = 0; i < hash.length; ++i) {
            if ((hash[i] & 255) < 16) {
                buf.append("0");
            }

            buf.append(Long.toString((long)(hash[i] & 255), 16));
        }

        return buf.toString();
    }

    public static final String randomString(int length) {
        if (length < 1) {
            return "";
        } else {
            char[] randBuffer = new char[length];

            for(int i = 0; i < randBuffer.length; ++i) {
                randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
            }

            return new String(randBuffer);
        }
    }

    public static final String randomIntegerString(int length) {
        if (length < 1) {
            return "";
        } else {
            char[] randBuffer = new char[length];

            for(int i = 0; i < randBuffer.length; ++i) {
                randBuffer[i] = numbers[randGen.nextInt(9)];
            }

            return new String(randBuffer);
        }
    }

    public static final String escapeForXML(String string) {
        if (string != null && string.length() != 0) {
            char[] sArray = string.toCharArray();
            StringBuffer buf = new StringBuffer(sArray.length);

            for(int i = 0; i < sArray.length; ++i) {
                char ch = sArray[i];
                if (ch == '<') {
                    buf.append("&lt;");
                } else if (ch == '>') {
                    buf.append("&gt;");
                } else if (ch == '&') {
                    buf.append("&amp;");
                } else if (ch == '"') {
                    buf.append("&quot;");
                } else {
                    buf.append(ch);
                }
            }

            return buf.toString();
        } else {
            return string;
        }
    }

    public static final String escapeForHTML(String input) {
        if (input != null && input.length() != 0) {
            StringBuffer buf = new StringBuffer(input.length());
            for(int i = 0; i < input.length(); ++i) {
                char ch = input.charAt(i);
                if (ch == '<') {
                    buf.append("&lt;");
                } else if (ch == '>') {
                    buf.append("&gt;");
                } else if (ch == '&') {
                    buf.append("&amp;");
                } else {
                    buf.append(ch);
                }
            }
            return buf.toString();
        } else {
            return input;
        }
    }

    public static boolean arrayContains(int[] arr, int num) {
        for(int i = 0; i < arr.length; ++i) {
            if (i == num) {
                return true;
            }
        }

        return false;
    }

    public static String[] split(String content, char separator) {
        ArrayList<String> al = new ArrayList();
        char[] ch = content.toCharArray();
        int k = 0;

        for(int i = 0; i < ch.length; ++i) {
            if (ch[i] == separator) {
                if (k < i) {
                    al.add(new String(ch, k, i - k));
                }

                k = i + 1;
            }
        }

        if (k < ch.length) {
            al.add(new String(ch, k, ch.length - k));
        }

        String[] ret = new String[al.size()];

        for(int i = 0; i < al.size(); ++i) {
            ret[i] = (String)al.get(i);
        }

        return ret;
    }

    public static String[] regDOTALLParseString(String strContent, String strRegEx) {
        Pattern myPattern = Pattern.compile(strRegEx, 32);
        Matcher oMatcher = myPattern.matcher(strContent);
        if (!oMatcher.matches()) {
            return new String[0];
        } else {
            int iLength = oMatcher.groupCount();
            ArrayList al = new ArrayList();

            for(int i = 0; i <= iLength; ++i) {
                try {
                    al.add(oMatcher.group(i));
                } catch (Exception var8) {
                    var8.printStackTrace();
                }
            }

            return (String[])((String[])al.toArray(new String[0]));
        }
    }

    public static boolean isNumberStr(String s) {
        if (s != null && s.trim().length() != 0) {
            s = s.trim();
            int count = 0;
            char[] ch = s.toCharArray();

            for(int i = 0; i < ch.length; ++i) {
                if (!Character.isDigit(ch[i])) {
                    if (ch[i] != '.') {
                        return false;
                    }

                    ++count;
                }
            }

            if (count < 2) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isValidIdentity(String s) {
        if (s != null && s.length() != 0) {
            char[] ch = s.toCharArray();
            if (ch[0] >= 'a' && ch[0] <= 'z' || ch[0] >= 'A' && ch[0] <= 'Z') {
                for(int i = 1; i < ch.length; ++i) {
                    if ((ch[i] < 'a' || ch[i] > 'z') && (ch[i] < 'A' || ch[i] > 'Z') && (ch[i] < '0' || ch[i] > '9') && ch[i] != '_' && ch[i] != '-') {
                        return false;
                    }
                }

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static String escape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);

        for(int i = 0; i < src.length(); ++i) {
            char j = src.charAt(i);
            if (!Character.isDigit(j) && !Character.isLowerCase(j) && !Character.isUpperCase(j)) {
                if (j < 256) {
                    tmp.append("%");
                    if (j < 16) {
                        tmp.append("0");
                    }

                    tmp.append(Integer.toString(j, 16));
                } else {
                    tmp.append("%u");
                    tmp.append(Integer.toString(j, 16));
                }
            } else {
                tmp.append(j);
            }
        }

        return tmp.toString();
    }

    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0;
        boolean var3 = false;

        while(lastPos < src.length()) {
            int pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                char ch;
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char)Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char)Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else if (pos == -1) {
                tmp.append(src.substring(lastPos));
                lastPos = src.length();
            } else {
                tmp.append(src.substring(lastPos, pos));
                lastPos = pos;
            }
        }

        return tmp.toString();
    }

    public static String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString() + "\n");
        StackTraceElement[] stacks = e.getStackTrace();
        return getStackTrace(stacks);
    }

    public static String getStackTrace(StackTraceElement[] stacks) {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < stacks.length; ++i) {
            sb.append("\t" + stacks[i].toString() + "\n");
        }

        return sb.toString();
    }

    public static String clearLetters(String str) {
        return str.replaceAll("[a-zA-Z]", "");
    }

    public static short getUnsignedByte(byte index) {
        return (short)(index & 255);
    }

    public static String hexDump(byte[] bytes, int fromIndex, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        } else if (length == 0) {
            return "";
        } else {
            int endIndex = fromIndex + length;
            char[] buf = new char[length << 1];
            int srcIdx = fromIndex;

            for(int dstIdx = 0; srcIdx < endIndex; dstIdx += 2) {
                System.arraycopy(HEXDUMP_TABLE, getUnsignedByte(bytes[srcIdx]) << 1, buf, dstIdx, 2);
                ++srcIdx;
            }

            return new String(buf);
        }
    }

    public static String arrayToString(String[] array, String token) {
        if (array == null) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();

            for(int i = 0; i < array.length; ++i) {
                sb.append(array[i]);
                if (i != array.length - 1) {
                    sb.append(token);
                }
            }

            return sb.toString();
        }
    }

    public static String arrayToString(long[] array, String token) {
        if (array == null) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();

            for(int i = 0; i < array.length; ++i) {
                sb.append(array[i]);
                if (i != array.length - 1) {
                    sb.append(token);
                }
            }

            return sb.toString();
        }
    }

    public static String arrayToString(int[] array, String token) {
        if (array == null) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();

            for(int i = 0; i < array.length; ++i) {
                sb.append(array[i]);
                if (i != array.length - 1) {
                    sb.append(token);
                }
            }

            return sb.toString();
        }
    }

    public static String arrayToString(short[] array, String token) {
        if (array == null) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();

            for(int i = 0; i < array.length; ++i) {
                sb.append(array[i]);
                if (i != array.length - 1) {
                    sb.append(token);
                }
            }

            return sb.toString();
        }
    }

    public static String arrayToString(float[] array, String token) {
        if (array == null) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();

            for(int i = 0; i < array.length; ++i) {
                sb.append(array[i]);
                if (i != array.length - 1) {
                    sb.append(token);
                }
            }

            return sb.toString();
        }
    }

    public static String native2ascii(String content) {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < content.length(); ++i) {
            char c = content.charAt(i);
            if (c > 255) {
                sb.append("\\\\u");
                int j = c >>> 8;
                String tmp = Integer.toHexString(j);
                if (tmp.length() == 1) {
                    sb.append("0");
                }

                sb.append(tmp);
                j = c & 255;
                tmp = Integer.toHexString(j);
                if (tmp.length() == 1) {
                    sb.append("0");
                }

                sb.append(tmp);
            } else {
                sb.append(c);
            }
        }

        return new String(sb);
    }

    public static String stackTraceToString(StackTraceElement[] stacks) {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < stacks.length; ++i) {
            sb.append("\t" + stacks[i].toString() + "\n");
        }

        return sb.toString();
    }

    public static String encode(String s, String srcEncoding, String dstEncoding) {
        try {
            byte[] bytes = s.getBytes(srcEncoding);
            return new String(bytes, dstEncoding);
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static String urlEncode(String url) {
        if (url.indexOf("?") == -1) {
            return url;
        } else {
            String paStr = url.split("\\?")[1];
            String[] params = paStr.split("&");
            StringBuffer sb = new StringBuffer();
            String[] arr$ = params;
            int len$ = params.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String p = arr$[i$];

                try {
                    String param = p.split("=")[0];
                    String value = p.split("=")[1];
                    sb.append(param);
                    sb.append("=");
                    sb.append(URLEncoder.encode(value));
                    sb.append("&");
                } catch (Exception var10) {
                    System.err.print(url);
                    var10.printStackTrace();
                }
            }

            String str = sb.toString();
            if (str.endsWith("&")) {
                str = str.substring(0, str.length() - 1);
            }

            if (url.indexOf("?") != -1) {
                url = url.split("\\?")[0] + "?" + str;
            }

            return url;
        }
    }

    public static String urlDecode(String content, String encoding) {
        try {
            return URLDecoder.decode(content, encoding);
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
            return content;
        }
    }

    public static String mapToString(Map map) {
        Object[] keys = map.keySet().toArray(new Object[0]);
        StringBuffer sb = new StringBuffer();
        Object[] arr$ = keys;
        int len$ = keys.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Object k = arr$[i$];
            String keystr = String.valueOf(k);
            Object value = map.get(k);
            if (value != null) {
                String vstr = String.valueOf(value);
                sb.append(keystr + "#@#" + vstr + "@#@");
            }
        }

        String str = sb.toString();
        if (str.length() > 2) {
            str = str.substring(0, str.length() - 3);
        }

        return str;
    }

    public static Map stringToMap(String mstr) {
        String[] str = mstr.split("@#@");
        HashMap<String, String> map = new HashMap();
        String[] arr$ = str;
        int len$ = str.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String s = arr$[i$];
            String[] ss = s.split("#@#");
            if (ss.length == 2) {
                map.put(ss[0], ss[1]);
            }
        }

        return map;
    }

    public static String propertiesToString(Properties map) {
        Object[] keys = map.keySet().toArray(new Object[0]);
        StringBuffer sb = new StringBuffer();
        Object[] arr$ = keys;
        int len$ = keys.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Object k = arr$[i$];
            String keystr = String.valueOf(k);
            Object value = map.get(k);
            if (value != null) {
                String vstr = String.valueOf(value);
                sb.append(keystr + "#@#" + vstr + "@#@");
            }
        }

        String str = sb.toString();
        if (str.length() > 2) {
            str = str.substring(0, str.length() - 3);
        }

        return str;
    }

    public static Properties stringToProperties(String mstr) {
        String[] str = mstr.split("@#@");
        Properties map = new Properties();
        String[] arr$ = str;
        int len$ = str.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String s = arr$[i$];
            String[] ss = s.split("#@#");
            if (ss.length == 2) {
                map.put(ss[0], ss[1]);
            }
        }

        return map;
    }

    public static Properties loadProperties(String file) {
        Properties props = new Properties();

        try {
            props.load(new FileInputStream(file));
        } catch (FileNotFoundException var3) {
            var3.printStackTrace();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return props;
    }

    public static String ascii2native(String content) {
        char[] cs = new char[content.length()];

        for(int i = 0; i < cs.length; ++i) {
            cs[i] = content.charAt(i);
        }

        return loadConvert(cs, 0, cs.length, new char[cs.length * 2]);
    }

    private static String loadConvert(char[] in, int off, int len, char[] convtBuf) {
        if (convtBuf.length < len) {
            int newLen = len * 2;
            if (newLen < 0) {
                newLen = 2147483647;
            }

            convtBuf = new char[newLen];
        }

        char[] out = convtBuf;
        int outLen = 0;
        int end = off + len;

        while(true) {
            while(true) {
                while(off < end) {
                    char aChar = in[off++];
                    if (aChar == '\\') {
                        aChar = in[off++];
                        if (aChar == 'u') {
                            int value = 0;

                            for(int i = 0; i < 4; ++i) {
                                aChar = in[off++];
                                switch(aChar) {
                                    case '0':
                                    case '1':
                                    case '2':
                                    case '3':
                                    case '4':
                                    case '5':
                                    case '6':
                                    case '7':
                                    case '8':
                                    case '9':
                                        value = (value << 4) + aChar - 48;
                                        break;
                                    case ':':
                                    case ';':
                                    case '<':
                                    case '=':
                                    case '>':
                                    case '?':
                                    case '@':
                                    case 'G':
                                    case 'H':
                                    case 'I':
                                    case 'J':
                                    case 'K':
                                    case 'L':
                                    case 'M':
                                    case 'N':
                                    case 'O':
                                    case 'P':
                                    case 'Q':
                                    case 'R':
                                    case 'S':
                                    case 'T':
                                    case 'U':
                                    case 'V':
                                    case 'W':
                                    case 'X':
                                    case 'Y':
                                    case 'Z':
                                    case '[':
                                    case '\\':
                                    case ']':
                                    case '^':
                                    case '_':
                                    case '`':
                                    default:
                                        throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                                    case 'A':
                                    case 'B':
                                    case 'C':
                                    case 'D':
                                    case 'E':
                                    case 'F':
                                        value = (value << 4) + 10 + aChar - 65;
                                        break;
                                    case 'a':
                                    case 'b':
                                    case 'c':
                                    case 'd':
                                    case 'e':
                                    case 'f':
                                        value = (value << 4) + 10 + aChar - 97;
                                }
                            }

                            out[outLen++] = (char)value;
                        } else {
                            if (aChar == 't') {
                                aChar = '\t';
                            } else if (aChar == 'r') {
                                aChar = '\r';
                            } else if (aChar == 'n') {
                                aChar = '\n';
                            } else if (aChar == 'f') {
                                aChar = '\f';
                            }

                            out[outLen++] = aChar;
                        }
                    } else {
                        out[outLen++] = aChar;
                    }
                }

                return new String(out, 0, outLen);
            }
        }
    }

    public static String formatNum(int value, int num) {
        String s;
        for(s = String.valueOf(value); s.length() < num; s = "0" + s) {
            ;
        }

        return s;
    }

    public static String formatNum(long value, int num) {
        String s;
        for(s = String.valueOf(value); s.length() < num; s = "0" + s) {
            ;
        }

        return s;
    }

    public static String concactAsNumer(String str1, String str2, int length) {
        int wlen = length - str1.length() - str2.length();
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < wlen; ++i) {
            sb.append("0");
        }

        String newstr = str1 + sb.toString() + str2;
        return newstr;
    }

    public static String encrypt(String strSrc, String encName) throws NoSuchAlgorithmException {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        if (encName == null || encName.equals("")) {
            encName = "SHA-256";
        }

        md = MessageDigest.getInstance(encName);
        md.update(bt);
        strDes = toHex(md.digest());
        return strDes;
    }

    public static String getAsciiCode(String content) {
        StringBuffer sb = new StringBuffer();
        int len = content.length();

        for(int i = 0; i < len; ++i) {
            char c = content.charAt(i);
            sb.append(String.valueOf(c));
        }

        return sb.toString();
    }

    public static String getAsciiCode4Low(String content) {
        StringBuffer sb = new StringBuffer();
        int len = content.length();

        for(int i = 0; i < len; ++i) {
            char c = content.charAt(i);
            sb.append(String.valueOf(c % 10));
        }

        return sb.toString();
    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static void main(String[] args) {
        String file = "E:/卧虎藏龙/服务器语言包20141029.xls";
        long now = System.currentTimeMillis();
        String md5 = getFileMD5(new File(file));
        System.out.println("[" + md5 + "] [" + (System.currentTimeMillis() - now) + "ms]");
    }

    static {
        char[] DIGITS = "0123456789abcdef".toCharArray();

        for(int i = 0; i < 256; ++i) {
            HEXDUMP_TABLE[(i << 1) + 0] = DIGITS[i >>> 4 & 15];
            HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i >>> 0 & 15];
        }

    }
}
