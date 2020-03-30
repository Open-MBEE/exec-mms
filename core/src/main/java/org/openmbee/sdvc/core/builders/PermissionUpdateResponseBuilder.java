package org.openmbee.sdvc.core.builders;

import org.openmbee.sdvc.core.objects.PermissionUpdateResponse;

import java.util.ArrayList;
import java.util.List;

public class PermissionUpdateResponseBuilder {

    private List<PermissionUpdateResponse.PermissionUpdate> permissionUpdates = new ArrayList<>();

    public PermissionUpdateResponseBuilder addPermissionUpdate(PermissionUpdateResponse.Action action, String name, String role, String orgId, String orgName,
                                                               String projectId, String projectName, String branchId, String branchName, boolean inherited) {
        permissionUpdates.add(new PermissionUpdateResponse.PermissionUpdate(action, name, role, orgId, orgName, projectId, projectName, branchId, branchName, inherited));
        return this;
    }

    public PermissionUpdateResponseBuilder addPermissionUpdate(PermissionUpdateResponse.Action action, String name, String role, String orgId, String orgName,
                                                               String projectId, String projectName, boolean inherited) {
        permissionUpdates.add(new PermissionUpdateResponse.PermissionUpdate(action, name, role, orgId, orgName, projectId, projectName, null, null, inherited));
        return this;
    }

    public PermissionUpdateResponseBuilder addPermissionUpdate(PermissionUpdateResponse.Action action, String name, String role, String orgId, String orgName, boolean inherited) {
        permissionUpdates.add(new PermissionUpdateResponse.PermissionUpdate(action, name, role, orgId, orgName, null, null, null, null, inherited));
        return this;
    }

    public PermissionUpdateResponseBuilder add(PermissionUpdateResponse updateUserPermissions) {
        permissionUpdates.addAll(updateUserPermissions.getPermissionUpdates());
        return this;
    }

    public PermissionUpdateResponse getPermissionUpdateReponse() {
        PermissionUpdateResponse response = new PermissionUpdateResponse();
        response.setPermissionUpdates(new ArrayList<>(permissionUpdates));
        return response;
    }
}
