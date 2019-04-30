package org.openmbee.sdvc.json;

public class ProjectJson extends BaseJson<ProjectJson> {

    public static final String ORGID = "orgId";
    public static final String PROJECTTYPE = "projectType";

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

    public ProjectJson setProjectType(String projectType) {
        this.put(PROJECTTYPE, projectType);
        return this;
    }

    public ProjectJson setOrgId(String orgId) {
        this.put(ORGID, orgId);
        return this;
    }
}
