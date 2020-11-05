package org.openmbee.mms.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties({"empty"})
@Schema(name = "Mount")
public class MountJson extends BaseJson<MountJson> {
}
