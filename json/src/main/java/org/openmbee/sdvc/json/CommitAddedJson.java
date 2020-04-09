package org.openmbee.sdvc.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"empty", BaseJson.REFID, BaseJson.COMMITID, BaseJson.PROJECTID,
    BaseJson.CREATOR, BaseJson.CREATED, BaseJson.MODIFIER, BaseJson.MODIFIED, BaseJson.NAME})
public class CommitAddedJson extends BaseJson<CommitAddedJson> {

}
