package org.openmbee.sdvc.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties({BaseJson.REFID, BaseJson.COMMITID, BaseJson.TYPE, "empty"})
@Schema(requiredProperties = {ProjectJson.ORGID, BaseJson.NAME})
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

    @Schema(description = "Acceptable values depends on the specific build and implementation (ex. default, cameo, jupyter), "
        + "if a value isn't supported, will fall back to default. This can influence the project's underlying schema used and "
        + "additional validation or processing when getting or updating elements.", defaultValue = "default")
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
