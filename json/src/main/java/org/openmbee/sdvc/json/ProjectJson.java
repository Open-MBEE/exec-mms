package org.openmbee.sdvc.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties({BaseJson.REFID, BaseJson.COMMITID, "empty"})
public class ProjectJson extends BaseJson<ProjectJson> {

    public static final String ORGID = "orgId";
    public static final String PROJECTTYPE = "projectType";
    public static final String ADDITIONALPROPERTIES = "additionalProperties";

    @Override
    public String getProjectId() {
        return (String) this.get(ID);
    }

    public String getOrgId() {
        return (String) this.get(ORGID);
    }

    public String getProjectType() {
        return (String) this.get(PROJECTTYPE);
    }

    public Map<String, Object> getAdditionalProperties() {
        return (Map<String, Object>) this.getOrDefault(ADDITIONALPROPERTIES, null);
    }

    public ProjectJson setProjectType(String projectType) {
        this.put(PROJECTTYPE, projectType);
        return this;
    }

    public ProjectJson setOrgId(String orgId) {
        this.put(ORGID, orgId);
        return this;
    }

    public ProjectJson setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.put(ADDITIONALPROPERTIES, additionalProperties);
        return this;
    }
}
