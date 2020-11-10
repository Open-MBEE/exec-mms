package org.openmbee.mms.crud.hooks;

import org.openmbee.mms.json.ElementJson;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public class ElementUpdateHook {

    public enum Action {ADD_UPDATE, DELETE;}
    private Action action;
    private String projectId;
    private String refId;
    private List<ElementJson> elements;
    private Map<String, String> params;
    private Authentication auth;

    public ElementUpdateHook(Action action, String projectId, String refId, List<ElementJson> elements, Map<String, String> params, Authentication auth) {
        this.action = action;
        this.projectId = projectId;
        this.refId = refId;
        this.elements = elements;
        this.params = params;
        this.auth = auth;
    }

    public Action getAction() {
        return action;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getRefId() {
        return refId;
    }

    public List<ElementJson> getElements() {
        return elements;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Authentication getAuth() {
        return auth;
    }
}
