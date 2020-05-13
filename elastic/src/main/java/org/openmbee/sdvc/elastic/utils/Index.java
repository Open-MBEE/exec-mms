package org.openmbee.sdvc.elastic.utils;

import org.openmbee.sdvc.core.config.ContextHolder;

public enum Index {

    BASE(""),
    NODE("_node"),
    COMMIT("_commit");

    private String suffix;

    Index(String suffix) {
        this.suffix = suffix;
    }

    public String get() {
        return ContextHolder.getContext().getProjectId().toLowerCase() + suffix;
    }
}
