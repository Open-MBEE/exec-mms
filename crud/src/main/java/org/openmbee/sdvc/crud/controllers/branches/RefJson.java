package org.openmbee.sdvc.crud.controllers.branches;

import org.openmbee.sdvc.crud.controllers.BaseJson;

public class RefJson extends BaseJson {

    public String getParentRefId() {
        return (String) this.get("parentRefId");
    }

    public String getParentCommitId() {
        return (String) this.get("parentCommitId");
    }
}
