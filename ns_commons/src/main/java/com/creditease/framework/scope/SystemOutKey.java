package com.creditease.framework.scope;

public enum SystemOutKey implements OutKey {
    RETURN_CODE,
    HTML_REDIRECT_URL,
    HTML_WINDOW_ONLOAD,
    HTML_SELF_CONTENT;

    private SystemOutKey() {
    }

    public String getDescription() {
        return null;
    }
}