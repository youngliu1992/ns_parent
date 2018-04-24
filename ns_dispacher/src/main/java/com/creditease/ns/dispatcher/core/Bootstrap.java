package com.creditease.ns.dispatcher.core;

import com.creditease.ns.dispatcher.community.http.HttpServer;
import com.creditease.ns.framework.startup.LifeCycle;
import com.creditease.ns.log.NsLog;
import com.creditease.ns.log.util.PrintUtil;
import java.util.ArrayList;
import java.util.List;

public class Bootstrap
{
    private static NsLog initLog = NsLog.getFramLog("Dispatcher", "分发器");

    public static void main(String[] args) throws Exception { PrintUtil.printNs4();
        PrintUtil.printJVM();
        initLog.info("开启式启动", new Object[0]);
        List startList = new ArrayList();
        startList.add(new HttpServer());

        for (LifeCycle startPart : startList) {
            startPart.startUp();
        }
    }
}