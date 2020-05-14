package org.openmbee.sdvc.search.controllers;

import java.util.Map;

public class BasicSearchRequest {

    private Map<String, String> params;
    private Map<String, String> recurse;

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getRecurse() {
        return recurse;
    }

    public void setRecurse(Map<String, String> recurse) {
        this.recurse = recurse;
    }
}
