package org.openmbee.sdvc.json;

public class ProjectJson extends BaseJson {

    public static final String ORGID = "orgId";

    @Override
    public String getProjectId() {
        return (String) this.get(ID);
    }

    public String getOrgId() {
        return (String) this.get(ORGID);
    }

    public BaseJson setOrgId(String orgId) {
        this.put(ORGID, orgId);
        return this;
    }
}
