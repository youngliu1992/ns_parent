package com.creditease.ns.chains.chain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.mvel2.UnresolveablePropertyException;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.BaseVariableResolverFactory;

public class JVMapVariableResolverFactory extends BaseVariableResolverFactory {
    protected Map<String, Object> variables;

    public JVMapVariableResolverFactory() {
        this.variables = new HashMap();
    }

    public JVMapVariableResolverFactory(Map variables) {
        this.variables = variables;
    }

    public JVMapVariableResolverFactory(Map<String, Object> variables, VariableResolverFactory nextFactory) {
        this.variables = variables;
        this.nextFactory = nextFactory;
    }

    public JVMapVariableResolverFactory(Map<String, Object> variables, boolean cachingSafe) {
        this.variables = variables;
    }

    public VariableResolver createVariable(String name, Object value) {
        try {
            VariableResolver vr;
            (vr = this.getVariableResolver(name)).setValue(value);
            return vr;
        } catch (UnresolveablePropertyException var5) {
            ProtoBufferValueMapVariableResolver vr;
            this.addResolver(name, vr = new ProtoBufferValueMapVariableResolver(this.variables, name)).setValue(value);
            return vr;
        }
    }

    public VariableResolver createVariable(String name, Object value, Class<?> type) {
        VariableResolver vr;
        try {
            vr = this.getVariableResolver(name);
        } catch (UnresolveablePropertyException var6) {
            vr = null;
        }

        if (vr != null && vr.getType() != null) {
            throw new RuntimeException("variable already defined within scope: " + vr.getType() + " " + name);
        } else {
            ProtoBufferValueMapVariableResolver vr;
            this.addResolver(name, vr = new ProtoBufferValueMapVariableResolver(this.variables, name, type)).setValue(value);
            return vr;
        }
    }

    public VariableResolver getVariableResolver(String name) {
        VariableResolver vr = (VariableResolver)this.variableResolvers.get(name);
        if (vr != null) {
            return vr;
        } else if (this.variables.containsKey(name)) {
            ProtoBufferValueMapVariableResolver vr;
            this.variableResolvers.put(name, vr = new ProtoBufferValueMapVariableResolver(this.variables, name));
            return vr;
        } else if (this.nextFactory != null) {
            return this.nextFactory.getVariableResolver(name);
        } else {
            throw new UnresolveablePropertyException("unable to resolve variable '" + name + "'");
        }
    }

    public boolean isResolveable(String name) {
        return this.variableResolvers.containsKey(name) || this.variables != null && this.variables.containsKey(name) || this.nextFactory != null && this.nextFactory.isResolveable(name);
    }

    protected VariableResolver addResolver(String name, VariableResolver vr) {
        this.variableResolvers.put(name, vr);
        return vr;
    }

    public boolean isTarget(String name) {
        return this.variableResolvers.containsKey(name);
    }

    public Set<String> getKnownVariables() {
        if (this.nextFactory == null) {
            return this.variables != null ? new HashSet(this.variables.keySet()) : new HashSet(0);
        } else {
            return this.variables != null ? new HashSet(this.variables.keySet()) : new HashSet(0);
        }
    }

    public void clear() {
        this.variableResolvers.clear();
        this.variables.clear();
    }
}
