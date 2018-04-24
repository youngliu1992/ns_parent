//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.creditease.ns.framework.spring;

import com.creditease.framework.work.ActionWorker;
import com.creditease.framework.work.Worker;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.spi.TransporterLog;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GenSpringPlugin implements LifeCycle, SpringPlugin {
    private static final String[] paths = new String[]{"classpath*:/config/spring/**/*applicationContext.xml", "classpath*:/spring/**/*applicationContext.xml", "classpath*:/spring/**/*applicationContext.xml", "classpath*:/spring/**/applicationContext-*.xml", "classpath*:/config/**/*applicationContext-*.xml", "classpath*:**/applicationContext.xml", "classpath*:**/*-applicationContext-*.xml", "classpath*:**/applicationContext-*.xml"};
    private static final String LOG_PREFIX = "GenSpring";
    private static ClassPathXmlApplicationContext context;

    public GenSpringPlugin() {
    }

    public synchronized void startUp() {
        if (context == null) {
            context = new ClassPathXmlApplicationContext(paths);
            TransporterLog.logSystemInfo("GenSpring [加载spring配置文件] [成功]", (Object[])null);
        }

    }

    public static ClassPathXmlApplicationContext getContext() {
        return context;
    }

    public void destroy() {
        context = null;
    }

    public Object getBean(String beanId) {
        return context.getBean(beanId);
    }

    public Object getBeanByClassName(Class<?> className) {
        boolean isAop = false;

        try {
            Class.forName("org.springframework.aop.Pointcut");
            isAop = true;
        } catch (ClassNotFoundException var13) {
            ;
        }

        if (isAop) {
            boolean isAopObject = false;
            String[] strs;
            String[] arr$;
            int len$;
            int i$;
            String str;
            Object proxy;
            Class target;
            if (!Worker.class.isAssignableFrom(className) && !ActionWorker.class.isAssignableFrom(className)) {
                strs = context.getBeanNamesForType(className);
                if (strs.length < 1) {
                    Class<?>[] interfaces = className.getInterfaces();

                    for(len$ = 0; len$ < interfaces.length; ++len$) {
                        strs = context.getBeanNamesForType(interfaces[len$]);
                        if (strs.length > 0) {
                            String[] arr$ = strs;
                            int len$ = strs.length;

                            for(int i$ = 0; i$ < len$; ++i$) {
                                String str = arr$[i$];
                                Object proxy = context.getBean(str);
                                isAopObject = AopUtils.isAopProxy(proxy);
                                if (isAopObject) {
                                    Class target = AopUtils.getTargetClass(proxy);
                                    if (target.isAssignableFrom(className)) {
                                        return proxy;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (strs.length == 1) {
                        return context.getBean(strs[0]);
                    }

                    arr$ = strs;
                    len$ = strs.length;

                    for(i$ = 0; i$ < len$; ++i$) {
                        str = arr$[i$];
                        proxy = context.getBean(str);
                        isAopObject = AopUtils.isAopProxy(proxy);
                        if (isAopObject) {
                            target = AopUtils.getTargetClass(proxy);
                            if (target.isAssignableFrom(className)) {
                                return proxy;
                            }
                        }
                    }
                }
            } else {
                strs = context.getBeanNamesForType(Worker.class);
                if (strs.length < 1) {
                    strs = context.getBeanNamesForType(ActionWorker.class);
                }

                arr$ = strs;
                len$ = strs.length;

                for(i$ = 0; i$ < len$; ++i$) {
                    str = arr$[i$];
                    proxy = context.getBean(str);
                    isAopObject = AopUtils.isAopProxy(proxy);
                    if (isAopObject) {
                        target = AopUtils.getTargetClass(proxy);
                        if (target.isAssignableFrom(className)) {
                            return proxy;
                        }
                    }
                }
            }
        }

        return context.getBean(className);
    }

    public void init() throws Exception {
        this.startUp();
    }
}
