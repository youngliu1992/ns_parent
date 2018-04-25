package com.creditease.ns.chains.util;

import java.util.Map;
import org.mvel2.MVEL;

public class ExpUtil {
    public ExpUtil() {
    }

    public static boolean checkCond(String cond, Map map) throws Exception {
        return ((Boolean)MVEL.eval(cond, map)).booleanValue();
    }
}
