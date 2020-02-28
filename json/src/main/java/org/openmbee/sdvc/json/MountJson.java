package org.openmbee.sdvc.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"empty"})
public class MountJson extends BaseJson<MountJson> {
}
