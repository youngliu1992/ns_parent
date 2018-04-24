//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.creditease.framework.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLUtil {
    public static final String BR = System.getProperty("line.separator");

    public XMLUtil() {
    }

    public static Document load(String xmlfile) throws Exception {
        File file = new File(xmlfile);
        return load(file);
    }

    public static Document load(File file) throws Exception {
        FileInputStream fr = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        fr.read(bytes);
        fr.close();
        String domContent = new String(bytes);
        return loadString(domContent);
    }

    public static Document load(InputStream input) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        String s = null;
        StringBuffer domContent = new StringBuffer();

        while((s = br.readLine()) != null) {
            domContent.append(s);
            domContent.append("\n");
        }

        br.close();
        return loadString(domContent.toString());
    }

    public static Document load(InputStream input, String charset) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(input, charset));
        String s = null;
        StringBuffer domContent = new StringBuffer();

        while((s = br.readLine()) != null) {
            domContent.append(s);
            domContent.append("\n");
        }

        br.close();
        return loadString(domContent.toString());
    }

    public static Document load(String fileName, String charset) throws Exception {
        File f = new File(fileName);
        if (!f.isFile()) {
            System.out.println("[文件不存在] [" + fileName + "]");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charset));
        String s = null;
        StringBuffer domContent = new StringBuffer();

        while((s = br.readLine()) != null) {
            domContent.append(s);
            domContent.append("\n");
        }

        br.close();
        return loadString(domContent.toString());
    }

    public static Document loadStringWithoutTitle(String domContent) throws Exception {
        domContent = "<?xml version=\"1.0\" encoding=\"" + System.getProperty("file.encoding") + "\"?>" + BR + domContent;
        return loadString(domContent);
    }

    public static Document parse(String domContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(false);
        factory.setIgnoringElementContentWhitespace(false);
        factory.setValidating(false);
        factory.setCoalescing(false);
        factory.setNamespaceAware(false);
        factory.setExpandEntityReferences(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        char[] chars = new char[domContent.length()];
        domContent.getChars(0, domContent.length(), chars, 0);
        InputSource is = new InputSource(new CharArrayReader(chars));
        return builder.parse(is);
    }

    public static Document loadString(String domContent) throws Exception {
        if (ConvertBOMFile.isBOMFormat(domContent.getBytes())) {
            domContent = ConvertBOMFile.trimBOM(domContent);
        }

        return parse(domContent);
    }

    public static void save(String xmlfile, Document dom) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(xmlfile));
        bw.write(toString(dom, System.getProperty("file.encoding")));
        bw.close();
    }

    public static String toString(Document dom, String encoding) throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer t = factory.newTransformer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(out);
        t.transform(new DOMSource(dom), result);
        return new String(out.toByteArray(), encoding);
    }

    public static Document blankDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(false);
        factory.setIgnoringElementContentWhitespace(false);
        factory.setValidating(false);
        factory.setCoalescing(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }

    public static String getChildText(Element parent, String name, Map<String, String> map) {
        Element e = getChildByName(parent, name);
        return e == null ? "" : getText(e, map);
    }

    public static Element getFirstChildrenByName(Element e, String name) {
        NodeList nl = e.getChildNodes();
        int max = nl.getLength();

        for(int i = 0; i < max; ++i) {
            Node n = nl.item(i);
            if (n.getNodeType() == 1 && n.getNodeName().indexOf(name) >= 0) {
                return (Element)n;
            }
        }

        return null;
    }

    public static String getFirstTextByName(Element e, String name, Map<String, String> map) {
        NodeList nl = e.getChildNodes();
        int max = nl.getLength();

        for(int i = 0; i < max; ++i) {
            Node n = nl.item(i);
            if (n.getNodeName().indexOf(name) >= 0) {
                return getText((Element)n, map);
            }
        }

        return null;
    }

    public static String getText(Element e, Map<String, String> map) {
        NodeList nl = e.getChildNodes();
        int max = nl.getLength();

        for(int i = 0; i < max; ++i) {
            Node n = nl.item(i);
            if (n.getNodeType() == 3) {
                String value = n.getNodeValue();
                if (value != null && value.trim().length() != 0 && map != null && !map.isEmpty() && map.containsKey(value.trim())) {
                    value = (String)map.get(value.trim());
                }

                return value;
            }
        }

        return "";
    }

    public static Element getChildByName(Element e, String name) {
        Element[] list = getChildrenByName(e, name);
        return list.length == 0 ? createChild(e.getOwnerDocument(), e, name) : list[0];
    }

    public static Element getStrictChildByName(Element e, String name) {
        Element[] list = getChildrenByName(e, name);
        if (list.length == 0) {
            return null;
        } else if (list.length > 1) {
            throw new IllegalStateException("Too many (" + list.length + ") '" + name + "' elements found!");
        } else {
            return list[0];
        }
    }

    public static String getAttributeAsString(Element e, String attribute, Map<String, String> map) {
        String s = e.getAttribute(attribute);
        if (s != null && s.trim().length() != 0) {
            if (map != null && !map.isEmpty() && map.containsKey(s.trim())) {
                s = (String)map.get(s.trim());
            }

            return s;
        } else {
            throw new IllegalArgumentException("attribute [" + attribute + "] not exists in element [" + e.getNodeName() + "]");
        }
    }

    public static String getAttributeAsString(Element e, String attribute, String defaultValue, Map<String, String> map) {
        try {
            return getAttributeAsString(e, attribute, map);
        } catch (Exception var5) {
            if (defaultValue != null && defaultValue.trim().length() != 0 && map != null && !map.isEmpty() && map.containsKey(defaultValue.trim())) {
                defaultValue = (String)map.get(defaultValue.trim());
            }

            return defaultValue;
        }
    }

    public static int getAttributeAsInteger(Element e, String attribute) {
        String s = e.getAttribute(attribute);
        if (s == null) {
            throw new IllegalArgumentException("attribute [" + attribute + "] not exists in element [" + e.getNodeName() + "]");
        } else {
            return Integer.parseInt(s);
        }
    }

    public static int getAttributeAsInteger(Element e, String attribute, int defaultValue) {
        try {
            return getAttributeAsInteger(e, attribute);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public static long getAttributeAsLong(Element e, String attribute) {
        String s = e.getAttribute(attribute);
        if (s == null) {
            throw new IllegalArgumentException("attribute [" + attribute + "] not exists in element [" + e.getNodeName() + "]");
        } else {
            return Long.parseLong(s);
        }
    }

    public static long getAttributeAsLong(Element e, String attribute, long defaultValue) {
        try {
            return getAttributeAsLong(e, attribute);
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public static boolean getAttributeAsBoolean(Element e, String attribute) {
        String s = e.getAttribute(attribute);
        if (s == null) {
            throw new IllegalArgumentException("attribute [" + attribute + "] not exists in element [" + e.getNodeName() + "]");
        } else if (s.equalsIgnoreCase("true")) {
            return true;
        } else if (s.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new IllegalArgumentException("attribute [" + attribute + "=" + s + "] is invalid value for boolean");
        }
    }

    public static boolean getAttributeAsBoolean(Element e, String attribute, boolean defaultValue) {
        try {
            return getAttributeAsBoolean(e, attribute);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public static String getValueAsString(Element e, Map<String, String> map) {
        String s = getText(e, map);
        if (s != null && s.trim().length() != 0) {
            return s;
        } else {
            throw new IllegalArgumentException("value not exists in element [" + e.getNodeName() + "]");
        }
    }

    public static String getValueAsString(Element e, String defaultValue, Map<String, String> map) {
        try {
            return getValueAsString(e, map);
        } catch (Exception var4) {
            if (defaultValue != null && defaultValue.trim().length() != 0 && map != null && !map.isEmpty() && map.containsKey(defaultValue.trim())) {
                defaultValue = (String)map.get(defaultValue.trim());
            }

            return defaultValue;
        }
    }

    public static int getValueAsInteger(Element e) {
        String s = getText(e, (Map)null);
        if (s == null) {
            throw new IllegalArgumentException("value not exists in element [" + e.getNodeName() + "]");
        } else {
            return Integer.parseInt(s);
        }
    }

    public static int getValueAsInteger(Element e, int defaultValue) {
        try {
            return getValueAsInteger(e);
        } catch (Exception var3) {
            return defaultValue;
        }
    }

    public static long getValueAsLong(Element e) {
        String s = getText(e, (Map)null);
        if (s == null) {
            throw new IllegalArgumentException("value not exists in element [" + e.getNodeName() + "]");
        } else {
            return Long.parseLong(s);
        }
    }

    public static long getValueAsLong(Element e, long defaultValue) {
        try {
            return getValueAsLong(e);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public static boolean getValueAsBoolean(Element e) {
        String s = getText(e, (Map)null);
        if (s == null) {
            throw new IllegalArgumentException("value not exists in element [" + e.getNodeName() + "]");
        } else if (s.equalsIgnoreCase("true")) {
            return true;
        } else if (s.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new IllegalArgumentException("value is invalid value for boolean");
        }
    }

    public static boolean getValueAsBoolean(Element e, boolean defaultValue) {
        try {
            return getValueAsBoolean(e);
        } catch (Exception var3) {
            return defaultValue;
        }
    }

    public static Element[] getChildrenByName(Element e, String name) {
        NodeList nl = e.getChildNodes();
        int max = nl.getLength();
        LinkedList list = new LinkedList();

        for(int i = 0; i < max; ++i) {
            Node n = nl.item(i);
            if (n.getNodeType() == 1 && n.getNodeName().equals(name)) {
                list.add(n);
            }
        }

        return (Element[])((Element[])list.toArray(new Element[list.size()]));
    }

    public static Element[] getChildren(Element e) {
        NodeList nl = e.getChildNodes();
        int max = nl.getLength();
        LinkedList list = new LinkedList();

        for(int i = 0; i < max; ++i) {
            Node n = nl.item(i);
            if (n.getNodeType() == 1) {
                list.add(n);
            }
        }

        return (Element[])((Element[])list.toArray(new Element[list.size()]));
    }

    public static Map getProperties(Element root, Map<String, String> map1) {
        Map map = new HashMap();
        Element[] list = getChildrenByName(root, "property");

        for(int i = 0; i < list.length; ++i) {
            String name = list[i].getAttribute("name");
            String type = list[i].getAttribute("type");
            String valueString = getText(list[i], map1);

            try {
                Class cls = Class.forName(type);
                Constructor con = cls.getConstructor(String.class);
                Object value = con.newInstance(valueString);
                map.put(name, value);
            } catch (Exception var11) {
                System.err.println("Unable to parse property '" + name + "'='" + valueString + "': " + var11.toString());
            }
        }

        return map;
    }

    public static String[] splitOnWhitespace(String source) {
        int pos = -1;
        LinkedList list = new LinkedList();
        int max = source.length();

        for(int i = 0; i < max; ++i) {
            char c = source.charAt(i);
            if (Character.isWhitespace(c)) {
                if (i - pos > 1) {
                    list.add(source.substring(pos + 1, i));
                }

                pos = i;
            }
        }

        return (String[])((String[])list.toArray(new String[list.size()]));
    }

    public static Element createChild(Document doc, Element root, String name) {
        Element elem = doc.createElement(name);
        root.appendChild(elem);
        return elem;
    }

    public static void createChildText(Document doc, Element elem, String name, String value) {
        Element child = doc.createElement(name);
        child.appendChild(doc.createTextNode(value == null ? "" : value));
        elem.appendChild(child);
    }

    public static void createOptionalChildText(Document doc, Element elem, String name, String value) {
        if (value != null && value.length() != 0) {
            Element child = doc.createElement(name);
            child.appendChild(doc.createTextNode(value));
            elem.appendChild(child);
        }
    }

    public static void applyProperties(Object o, Element root, Map<String, String> map1) {
        Map map = getProperties(root, map1);
        Iterator it = map.keySet().iterator();
        Field[] fields = o.getClass().getFields();
        Method[] methods = o.getClass().getMethods();

        while(it.hasNext()) {
            String name = (String)it.next();
            Object value = map.get(name);

            try {
                int i;
                for(i = 0; i < fields.length; ++i) {
                    if (fields[i].getName().equalsIgnoreCase(name) && isTypeMatch(fields[i].getType(), value.getClass())) {
                        fields[i].set(o, value);
                        System.err.println("Set field " + fields[i].getName() + "=" + value);
                        break;
                    }
                }

                for(i = 0; i < methods.length; ++i) {
                    if (methods[i].getName().equalsIgnoreCase("set" + name) && methods[i].getParameterTypes().length == 1 && isTypeMatch(methods[i].getParameterTypes()[0], value.getClass())) {
                        methods[i].invoke(o, value);
                        System.err.println("Set method " + methods[i].getName() + "=" + value);
                        break;
                    }
                }
            } catch (Exception var10) {
                System.err.println("Unable to apply property '" + name + "': " + var10.toString());
            }
        }

    }

    private static boolean isTypeMatch(Class one, Class two) {
        if (one.equals(two)) {
            return true;
        } else {
            if (one.isPrimitive()) {
                if (one.getName().equals("int") && two.getName().equals("java.lang.Integer")) {
                    return true;
                }

                if (one.getName().equals("long") && two.getName().equals("java.lang.Long")) {
                    return true;
                }

                if (one.getName().equals("float") && two.getName().equals("java.lang.Float")) {
                    return true;
                }

                if (one.getName().equals("double") && two.getName().equals("java.lang.Double")) {
                    return true;
                }

                if (one.getName().equals("char") && two.getName().equals("java.lang.Character")) {
                    return true;
                }

                if (one.getName().equals("byte") && two.getName().equals("java.lang.Byte")) {
                    return true;
                }

                if (one.getName().equals("short") && two.getName().equals("java.lang.Short")) {
                    return true;
                }

                if (one.getName().equals("boolean") && two.getName().equals("java.lang.Boolean")) {
                    return true;
                }
            }

            return false;
        }
    }
}
