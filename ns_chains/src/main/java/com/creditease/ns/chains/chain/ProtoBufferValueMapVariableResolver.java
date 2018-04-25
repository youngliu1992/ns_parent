package com.creditease.ns.chains.chain;

import com.creditease.framework.util.ProtoStuffSerializeUtil;
import java.util.Map;
import org.mvel2.DataConversion;
import org.mvel2.integration.VariableResolver;

public class ProtoBufferValueMapVariableResolver implements VariableResolver {
    private String name;
    private Class<?> knownType;
    private Map<String, Object> variableMap;

    public ProtoBufferValueMapVariableResolver(Map<String, Object> variableMap, String name) {
        this.variableMap = variableMap;
        this.name = name;
    }

    public ProtoBufferValueMapVariableResolver(Map<String, Object> variableMap, String name, Class knownType) {
        this.name = name;
        this.knownType = knownType;
        this.variableMap = variableMap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStaticType(Class knownType) {
        this.knownType = knownType;
    }

    public void setVariableMap(Map<String, Object> variableMap) {
        this.variableMap = variableMap;
    }

    public String getName() {
        return this.name;
    }

    public Class getType() {
        return this.knownType;
    }

    public void setValue(Object value) {
        if (this.knownType != null && value != null && value.getClass() != this.knownType) {
            if (!DataConversion.canConvert(this.knownType, value.getClass())) {
                throw new RuntimeException("cannot assign " + value.getClass().getName() + " to type: " + this.knownType.getName());
            }

            try {
                value = DataConversion.convert(value, this.knownType);
            } catch (Exception var3) {
                throw new RuntimeException("cannot convert value of " + value.getClass().getName() + " to: " + this.knownType.getName());
            }
        }

        this.variableMap.put(this.name, value);
    }

    public Object getValue() {
        try {
            Object o = ProtoStuffSerializeUtil.unSerializeForCommon((byte[])((byte[])this.variableMap.get(this.name)));
            return o;
        } catch (Exception var2) {
            throw new IllegalArgumentException("name:" + this.name + "对应的值不是基础数据类型，不能进行比较，value:" + this.variableMap.get(this.name));
        }
    }

    public int getFlags() {
        return 0;
    }
}
