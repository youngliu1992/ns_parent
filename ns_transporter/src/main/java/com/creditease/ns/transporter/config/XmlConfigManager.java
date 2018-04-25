package com.creditease.ns.transporter.config;

import com.creditease.framework.util.FileUtils;
import com.creditease.framework.util.XMLUtil;
import com.creditease.framework.work.Worker;
import com.creditease.ns.chains.context.GlobalScope;
import com.creditease.ns.chains.def.CatalogDef;
import com.creditease.ns.chains.def.ElementDef;
import com.creditease.ns.framework.spring.SpringPlugin;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.transporter.context.XmlAppTransporterContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlConfigManager implements LifeCycle, ConfigManager {
    public static NsLog frameLog;
    private boolean isStarted;
    private boolean isLoaded;
    private String resourcePath;
    private Map<String, InQueueInfo> queueNameToQueueInfos = new LinkedHashMap();
    private static final String DEFAULT_RESOURCE_LOCATION = "nstransporter.xml";
    public static int DEFAULT_BUFFERSIZE;
    public static int DEFAULT_HANDLER_NUM;
    private Document doc;
    private static XmlConfigManager self;
    private XmlAppTransporterContext xmlAppTransporterContext = null;
    private boolean isSpring;

    public void init() {
        self = this;
    }

    public synchronized void loadConfig() {
        long startTime = System.currentTimeMillis();
        if (!this.isLoaded) {
            if (this.resourcePath == null) {
                this.resourcePath = "nstransporter.xml";
            }

            String configPath = FileUtils.convertToAbsolutePath(this.resourcePath);

            try {
                this.doc = XMLUtil.load(configPath);

                try {
                    this.loadInQueueInfos();
                } catch (Exception var5) {
                    var5.printStackTrace();
                    throw var5;
                }

                frameLog.debug("# 加载配置文件成功 resourcePath:{} configPath:{} #", new Object[]{this.resourcePath, configPath, System.currentTimeMillis() - startTime});
            } catch (Exception var6) {
                frameLog.error("# 加载配置文件失败 出现异常 resourcePath:{} configPath:{} #", new Object[]{this.resourcePath, configPath, System.currentTimeMillis() - startTime, var6});
                throw new RuntimeException("加载配置文件" + this.resourcePath + "出错");
            }

            this.isLoaded = true;
        }

    }

    public synchronized void startUp() {
        if (!this.isStarted) {
            this.loadConfig();
            this.isStarted = true;
        }

    }

    private XmlConfigManager() {
    }

    public static ConfigManager getInstance() {
        return self;
    }

    private void loadInQueueInfos() throws Exception {
        Element root = this.doc.getDocumentElement();
        Element prefix = XMLUtil.getStrictChildByName(root, "prefix");
        String queuePrefix = "";
        if (prefix != null) {
            XMLUtil.getText(prefix, (Map)null);
        }

        Element launchersElement = XMLUtil.getStrictChildByName(root, "launchers");
        SpringPlugin springPlugin = null;
        String refCatalogId;
        int handlerSize;
        if (launchersElement != null) {
            Element[] launchers = XMLUtil.getChildrenByName(launchersElement, "launcher");
            if (launchers.length == 0) {
                frameLog.error("# 解析配置文件失败 launchers元素下必须配置至少一个launcher元素 root:{} launchersElement:{} #", new Object[]{root, launchersElement});
                throw new Exception("配置文件配置错误，必须在launchers下配置launcher的信息!");
            }

            for(int i = 0; i < launchers.length; ++i) {
                Element launcherElement = launchers[i];
                if (launcherElement == null) {
                    frameLog.error("# 解析配置文件失败,launcher信息配置有误 root:{} launchersElement:{} #", new Object[]{root, launchersElement});
                    throw new Exception("配置文件配置错误，必须在launchers下配置完整的launcher的信息!");
                }

                Element classElement = XMLUtil.getStrictChildByName(launcherElement, "class");
                if (classElement == null) {
                    frameLog.error("# 解析配置文件失败,launcher元素下必须指定一个class元素 root:{} launchersElement:{} #", new Object[]{root, launchersElement});
                    throw new Exception("配置文件配置错误，必须在launcher下配置完整的class的信息!");
                }

                String launcherClassName = classElement.getAttribute("name");
                if (launcherClassName == null || launcherClassName.trim().length() < 1) {
                    frameLog.error("# 解析配置文件失败,class元素必须指定name属性 root:{} launchersElement:{} launcherElement:{} #", new Object[]{root, launchersElement, launcherElement});
                    throw new Exception("配置文件配置错误，launcher必须指定对应的class!");
                }

                Class cl = null;

                try {
                    cl = Class.forName(launcherClassName);
                } catch (ClassNotFoundException var31) {
                    frameLog.error("# 解析配置文件失败,没有找到对应的class root:{} launchersElement:{} launcherElement:{} className:{} #", new Object[]{root, launchersElement, launcherElement, launcherClassName});
                    throw new Exception("配置文件配置错误，没有找到对应的class" + launcherClassName + "!");
                }

                refCatalogId = classElement.getAttribute("method");
                if (refCatalogId != null && refCatalogId.trim().length() >= 1) {
                    Object o = null;

                    try {
                        Constructor[] constructors = cl.getDeclaredConstructors();
                        Constructor[] arr$ = constructors;
                        int len$ = constructors.length;
                        handlerSize = 0;

                        while(true) {
                            if (handlerSize < len$) {
                                Constructor constructor = arr$[handlerSize];
                                Class[] cls = constructor.getParameterTypes();
                                if (cls != null && cls.length >= 1) {
                                    ++handlerSize;
                                    continue;
                                }

                                constructor.setAccessible(true);
                                o = constructor.newInstance();
                            }

                            if (o == null) {
                                throw new Exception(cl.getName() + " 无参构造函数!");
                            }

                            if (o instanceof SpringPlugin) {
                                this.isSpring = true;
                                springPlugin = (SpringPlugin)o;
                            }
                            break;
                        }
                    } catch (Exception var32) {
                        frameLog.error("# 解析配置文件失败,指定的class没有对应的无参构造函数 root:{} launchersElement:{} launcherElement:{} className:{} #", new Object[]{root, launchersElement, launcherElement, launcherClassName, var32});
                        throw new Exception("没有找到对应的class" + launcherClassName + "的无参构造函数!");
                    }

                    this.checkAndExecuteMethod(root, launchersElement, launcherElement, launcherClassName, cl, refCatalogId, o);
                } else {
                    refCatalogId = classElement.getAttribute("static-method");
                    if (refCatalogId == null || refCatalogId.trim().length() < 1) {
                        frameLog.error("# 解析配置文件失败,class元素必须指定method属性或者static-method属性 root:{} launchersElement:{} launcherElement:{} className:{} #", new Object[]{root, launchersElement, launcherElement, launcherClassName});
                        throw new Exception("配置文件配置错误，没有找到对应的class" + launcherClassName + "的配置的method!");
                    }

                    this.checkAndExecuteMethod(root, launchersElement, launcherElement, launcherClassName, cl, refCatalogId, (Object)null);
                }
            }
        }

        Element inqueuesElement = XMLUtil.getStrictChildByName(root, "inqueues");
        if (inqueuesElement == null) {
            frameLog.error("# 解析配置文件失败,没有配置inqueues元素 root:{} inqueuesElement:{} #", new Object[]{root, inqueuesElement});
            throw new Exception("配置文件配置错误，必须配置inqueues元素的信息!");
        } else {
            Element[] inqueues = XMLUtil.getChildrenByName(inqueuesElement, "queue");
            if (inqueues.length == 0) {
                frameLog.error("# 解析配置文件失败,inqueues元素下必须配置至少一个queue元素 root:{} inqueuesElement:{} #", new Object[]{root, inqueuesElement});
                throw new Exception("配置文件配置错误，必须配置inqueue的信息!");
            } else {
                Element[] arr$ = inqueues;
                int len$ = inqueues.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    Element e = arr$[i$];
                    refCatalogId = e.getAttribute("refCatalog");
                    Element nameElement = XMLUtil.getStrictChildByName(e, "name");
                    if (nameElement == null) {
                        frameLog.error("# 解析配置文件失败,queue元素下必须指明name元素 root:{} inqueuesElement:{} e:{} #", new Object[]{root, inqueuesElement, e});
                        throw new Exception("配置文件配置错误，必须配置对应queue的名称!");
                    }

                    String queueName = XMLUtil.getValueAsString(nameElement, (Map)null);
                    if (queuePrefix != null && queuePrefix.trim().length() > 0) {
                        queueName = queuePrefix + "_" + queueName;
                    }

                    if (queueName == null || queueName.trim().length() < 1) {
                        frameLog.error("# 解析配置文件失败,name元素必须有值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName});
                        throw new Exception("配置文件配置错误，必须配置对应queue的名称!");
                    }

                    if (this.queueNameToQueueInfos.containsKey(queueName)) {
                        frameLog.error("# 解析配置文件失败,发现name元素值重复 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName});
                        throw new Exception("配置文件配置错误，队列名称" + queueName + "不能重复!");
                    }

                    int buffersize = DEFAULT_BUFFERSIZE;
                    Element bufferSizeElement = XMLUtil.getStrictChildByName(e, "buffersize");
                    if (bufferSizeElement != null) {
                        String buffsize = XMLUtil.getValueAsString(bufferSizeElement, (Map)null);
                        if (buffsize != null && buffsize.trim().length() > 0) {
                            try {
                                buffersize = Integer.parseInt(buffsize);
                                if (buffersize > 1000000) {
                                    buffersize = 1000000;
                                    frameLog.debug("# 解析配置文件,解析buffersize元素 出现大于1000000的buffersize配置 使用最大值1000000 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} buffsize:{} buffersize:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, buffsize, buffersize});
                                } else if (buffersize <= 0) {
                                    buffersize = DEFAULT_BUFFERSIZE;
                                    frameLog.debug("# 解析配置文件,解析buffersize元素 出现小于1的buffersize配置 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} buffsize:{} buffersize:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, buffsize, buffersize});
                                }
                            } catch (Exception var30) {
                                frameLog.debug("# 解析配置文件失败,解析buffersize元素出现异常 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} buffsize:{} buffersize:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, buffsize, buffersize, var30});
                            }
                        } else {
                            frameLog.debug("# 解析配置文件,没有配置buffersize 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} buffsize:{} buffersize:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, buffsize, buffersize});
                        }
                    } else {
                        frameLog.debug("# 解析配置文件,没有配置buffersize 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} buffersize:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, buffersize});
                    }

                    handlerSize = DEFAULT_HANDLER_NUM;
                    Element handlerSizeElement = XMLUtil.getStrictChildByName(e, "handlersize");
                    if (handlerSizeElement != null) {
                        String handlesize = XMLUtil.getValueAsString(handlerSizeElement, (Map)null);
                        if (handlesize != null && handlesize.trim().length() > 0) {
                            handlerSize = Integer.parseInt(handlesize);

                            try {
                                if (handlerSize > 100) {
                                    handlerSize = 100;
                                    frameLog.debug("# 解析配置文件,解析handlersize元素 出现大于100的handlerSize配置 使用最大值100 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} handlesize:{} handlerSize:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, handlesize, handlerSize});
                                } else if (handlerSize <= 0) {
                                    handlerSize = DEFAULT_HANDLER_NUM;
                                    frameLog.debug("# 解析配置文件,解析handlersize元素 出现小于1的buffersize配置 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} handlesize:{} handlerSize:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, handlesize, handlerSize});
                                }
                            } catch (Exception var29) {
                                frameLog.debug("# 解析配置文件,解析handlersize元素 失败 出现异常 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} handlesize:{} handlersize:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, handlesize, handlerSize, var29});
                            }
                        } else {
                            frameLog.debug("# 解析配置文件,解析handlersize元素 没有配置handlersize元素 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} handlesize:{} handlersize:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, handlesize, handlerSize});
                        }
                    } else {
                        frameLog.debug("# 解析配置文件 没有配置handlersize 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} handlersize:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, handlerSize});
                    }

                    Element fetcherNumElement = XMLUtil.getStrictChildByName(e, "fetchernum");
                    int fetcherNum = 1;
                    if (fetcherNumElement != null) {
                        String fetchernum = XMLUtil.getValueAsString(fetcherNumElement, (Map)null);
                        if (fetchernum != null && fetchernum.trim().length() > 0) {
                            fetcherNum = Integer.parseInt(fetchernum);

                            try {
                                if (fetcherNum > 100) {
                                    fetcherNum = 100;
                                    frameLog.debug("# 解析配置文件 解析fetchernum元素 出现大于100的fetchernum配置 使用最大值100 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} fetchernum:{} fetcherNum:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, fetchernum, fetcherNum});
                                } else if (fetcherNum <= 0) {
                                    fetcherNum = 1;
                                    frameLog.debug("# 解析配置文件 解析fetchernum元素 出现小于1的buffersize配置 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} fetchernum:{} fetcherNum:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, fetchernum, fetcherNum});
                                }
                            } catch (Exception var28) {
                                frameLog.debug("# 解析配置文件 解析fetchernum元素 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} fetchernum:{} fetcherNum:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, fetchernum, fetcherNum, var28});
                            }
                        } else {
                            frameLog.debug("# 解析配置文件 解析fetchernum元素 配置异常 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} fetchernum:{} fetcherNum:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, fetchernum, fetcherNum});
                        }
                    } else {
                        frameLog.debug("# 解析配置文件 没有配置fetchernum 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} fetchernum:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, fetcherNum});
                    }

                    Element senderNumElement = XMLUtil.getStrictChildByName(e, "sendernum");
                    int senderNum = 1;
                    String serviceClassName;
                    if (senderNumElement != null) {
                        serviceClassName = XMLUtil.getValueAsString(senderNumElement, (Map)null);
                        if (serviceClassName != null && serviceClassName.trim().length() > 0) {
                            senderNum = Integer.parseInt(serviceClassName);

                            try {
                                if (senderNum > 100) {
                                    senderNum = 100;
                                    frameLog.debug("# 解析配置文件 解析sendernum元素 出现大于100的senderNum配置 使用最大值100 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} sendernum:{} senderNum:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, serviceClassName, senderNum});
                                } else if (senderNum <= 0) {
                                    senderNum = 1;
                                    frameLog.debug("# 解析配置文件 解析sendernum元素 出现小于1的buffersize配置 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} sendernum:{} senderNum:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, serviceClassName, senderNum});
                                }
                            } catch (Exception var27) {
                                frameLog.debug("# 解析配置文件 解析sendernum元素 失败 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} sendernum:{} senderNum:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, serviceClassName, senderNum, var27});
                            }
                        } else {
                            frameLog.debug("# 解析配置文件 解析sendernum元素 配置异常 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} sendernum:{} senderNum:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, serviceClassName, senderNum});
                        }
                    } else {
                        frameLog.debug("# 解析配置文件 没有配置sendernum 使用默认值 root:{} inqueuesElement:{} e:{} nameElement:{} queueName:{} sendernum:{} #", new Object[]{root, inqueuesElement, e, nameElement, queueName, senderNum});
                    }

                    serviceClassName = "com.creditease.ns.transporter.chain.service.DefaultServiceChainBridge";
                    Element serviceClassElement = XMLUtil.getStrictChildByName(e, "serviceClass");
                    if (refCatalogId != null && refCatalogId.trim().length() >= 1) {
                        if (serviceClassElement != null) {
                            frameLog.error("# 解析配置文件失败,指定了refCatalog不能配置serviceClass元素 root:{} inqueuesElement:{} e:{} #", new Object[]{root, inqueuesElement, e});
                            throw new Exception("配置文件配置错误，指定了refCatalog就不能配置自定义的serviceClass!");
                        }

                        this.checkCatalogId(refCatalogId);
                    } else {
                        if (serviceClassElement == null) {
                            frameLog.error("# 解析配置文件失败,没有配置serviceClass元素 root:{} inqueuesElement:{} e:{} #", new Object[]{root, inqueuesElement, e});
                            throw new Exception("配置文件配置错误，必须配置对应queue的serviceClass!");
                        }

                        serviceClassName = XMLUtil.getValueAsString(serviceClassElement, (Map)null);
                        if (serviceClassName == null || serviceClassName.trim().length() < 1) {
                            frameLog.error("# 解析配置文件失败,没有指定serviceClass元素的值 root:{} inqueuesElement:{} e:{} nameElement:{} serviceClass:{} #", new Object[]{root, inqueuesElement, e, serviceClassElement, serviceClassName});
                            throw new Exception("配置文件配置错误，必须配置对应serviceClass的名称!");
                        }
                    }

                    InQueueInfo inQueueInfo = new InQueueInfo();
                    inQueueInfo.setQueueName(queueName);
                    inQueueInfo.setBufferSize(buffersize);
                    inQueueInfo.setHandlerNum(handlerSize);
                    inQueueInfo.setServiceClassName(serviceClassName);
                    inQueueInfo.setFetcherNum(fetcherNum);
                    inQueueInfo.setSenderNum(senderNum);
                    if (refCatalogId != null && refCatalogId.trim().length() > 0) {
                        inQueueInfo.setRefCatalogId(refCatalogId);
                    }

                    if (springPlugin != null) {
                        inQueueInfo.setSpringPlugin(springPlugin);
                    }

                    this.queueNameToQueueInfos.put(queueName, inQueueInfo);
                    this.checkServiceClass();
                    frameLog.debug("# 解析配置文件 加载队列{}配置 成功 buffersize:{} handlersize:{} serviceClassName:{} #", new Object[]{queueName, buffersize, handlerSize, serviceClassName});
                }

            }
        }
    }

    private void checkAndExecuteMethod(Element root, Element launchersElement, Element launcherElement, String launcherClassName, Class cl, String methodName, Object target) throws Exception, IllegalAccessException, InvocationTargetException {
        Method[] methods = cl.getDeclaredMethods();
        Method targetMethod = null;
        Method[] arr$ = methods;
        int len$ = methods.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Method m = arr$[i$];
            if (methodName.equals(m.getName())) {
                if (targetMethod != null) {
                    frameLog.error("# 解析配置文件失败,launcher的class的启动方法不能有重名 root:{} launchersElement:{} launcherElement:{} className:{} method:{} #", new Object[]{root, launchersElement, launcherElement, launcherClassName, methodName});
                    throw new Exception("配置文件配置错误，class" + launcherClassName + "的配置的method" + methodName + "有重名方法!");
                }

                targetMethod = m;
            }
        }

        if (targetMethod == null) {
            frameLog.error("# 解析配置文件失败,launcher的class中没有找到启动方法 root:{} launchersElement:{} launcherElement:{} className:{} method:{} #", new Object[]{root, launchersElement, launcherElement, launcherClassName, methodName});
            throw new Exception("配置文件配置错误，class" + launcherClassName + "的配置的method" + methodName + "没有找到!");
        } else {
            targetMethod.setAccessible(true);
            targetMethod.invoke(target);
        }
    }

    private void checkServiceClass() {
        Iterator iterator = this.queueNameToQueueInfos.keySet().iterator();

        while(iterator.hasNext()) {
            String queueName = (String)iterator.next();
            InQueueInfo inQueueInfo = (InQueueInfo)this.queueNameToQueueInfos.get(queueName);
            String serviceName = inQueueInfo.getServiceClassName();

            try {
                boolean hasNoParamConstrutctor = false;
                Class cl = Class.forName(serviceName);
                Constructor[] constructors = cl.getConstructors();

                for(int i = 0; i < constructors.length; ++i) {
                    Class[] cls = constructors[i].getParameterTypes();
                    if (cls.length == 0) {
                        hasNoParamConstrutctor = true;
                        break;
                    }
                }

                if (!hasNoParamConstrutctor) {
                    frameLog.error("检查配置的Service 失败 没有无参构造函数 queueName:{} serviceName:{}", new Object[]{queueName, serviceName});
                    throw new RuntimeException("service:" + serviceName + "没有默认的无参构造函数，请添加!");
                }

                if (!Worker.class.isAssignableFrom(cl)) {
                    frameLog.error("检查配置的Service 失败 serviceClass没有实现worker接口 queueName:{} serviceName:{}", new Object[]{queueName, serviceName});
                    throw new RuntimeException("service:" + serviceName + "没有实现defaultWorker接口，请实现!");
                }
            } catch (ClassNotFoundException var10) {
                frameLog.error("检查配置的Service 失败 没有对应的class queueName:{} serviceName:{}", new Object[]{queueName, serviceName, var10});
                throw new RuntimeException("配置的serviceclass:" + serviceName + "没有对应的classs");
            }
        }

    }

    private void checkCatalogId(String catalogId) throws Exception {
        GlobalScope globalScope = GlobalScope.getInstance();
        ElementDef elementDef = globalScope.getGlobalScopeElement(catalogId);
        if (elementDef == null) {
            throw new Exception("配置的" + catalogId + "不存在在当前的任何chains的配置文件中,可能是nschains没有启动或者没有配置此catalog");
        } else if (!(elementDef instanceof CatalogDef)) {
            throw new Exception("配置的" + catalogId + "是" + elementDef.getClass().getName() + "元素,不是catalog,配置只能引用catalog元素");
        }
    }

    public Map<String, InQueueInfo> getQueueNameToQueueInfos() {
        return Collections.unmodifiableMap(this.queueNameToQueueInfos);
    }

    public void setQueueNameToQueueInfos(Map<String, InQueueInfo> queueNameToQueueInfos) {
        this.queueNameToQueueInfos = queueNameToQueueInfos;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getServiceClass(String queueName) {
        String serviceClass = ((InQueueInfo)this.queueNameToQueueInfos.get(queueName)).getServiceClassName();
        return serviceClass;
    }

    public void destroy() {
        long startTime = System.currentTimeMillis();
        System.out.println("DefaultConfigManager 开始关闭");
        this.isStarted = false;
        this.isSpring = false;
        this.isLoaded = false;
        Iterator it = this.queueNameToQueueInfos.keySet().iterator();

        while(it.hasNext()) {
            String queueName = (String)it.next();
            this.queueNameToQueueInfos.remove(queueName);
        }

        this.queueNameToQueueInfos = null;
        this.doc = null;
        this.resourcePath = null;
        this.xmlAppTransporterContext = null;
        frameLog.info("DefaultConfigManager 关闭 成功 cost:" + (System.currentTimeMillis() - startTime) + "ms", new Object[0]);
    }

    public XmlAppTransporterContext getXmlAppTransporterContext() {
        return this.xmlAppTransporterContext;
    }

    public void setXmlAppTransporterContext(XmlAppTransporterContext xmlAppTransporterContext) {
        this.xmlAppTransporterContext = xmlAppTransporterContext;
    }

    public boolean isSpring() {
        return this.isSpring;
    }

    public void setSpring(boolean isSpring) {
        this.isSpring = isSpring;
    }

    static {
        frameLog = XmlAppTransporterContext.frameLog;
        DEFAULT_BUFFERSIZE = 100;
        DEFAULT_HANDLER_NUM = 10;
        self = new XmlConfigManager();
    }
}
