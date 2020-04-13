package org.openmbee.sdvc.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties({BaseJson.COMMITID, BaseJson.REFID, BaseJson.PROJECTID, BaseJson.TYPE, "empty"})
@Schema(requiredProperties = {BaseJson.NAME})
public class OrgJson extends BaseJson<OrgJson> {
}
