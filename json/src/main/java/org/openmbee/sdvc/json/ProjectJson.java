package org.openmbee.sdvc.json;

public class ProjectJson extends BaseJson {

    @Override
    public String getProjectId() {
        return (String) this.get(ID);
    }
}
