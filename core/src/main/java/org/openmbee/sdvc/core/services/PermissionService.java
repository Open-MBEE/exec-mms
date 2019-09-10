package org.openmbee.sdvc.core.services;

import org.openmbee.sdvc.core.objects.PermissionUpdateRequest;

public interface PermissionService {

    void initOrgPerms(String orgId, String creator);

    void initProjectPerms(String projectId, boolean inherit, String creator);

    void initBranchPerms(String projectId, String branchId, boolean inherit, String creator);

    void updateOrgUserPerms(PermissionUpdateRequest req, String orgId);

    void updateOrgGroupPerms(PermissionUpdateRequest req, String orgId);

    void updateProjectUserPerms(PermissionUpdateRequest req, String projectId);

    void updateProjectGroupPerms(PermissionUpdateRequest req, String projectId);

    void updateBranchUserPerms(PermissionUpdateRequest req, String projectId, String branchId);

    void updateBranchGroupPerms(PermissionUpdateRequest req, String projectId, String branchId);

    void setProjectInherit(boolean isInherit, String projectId);

    void setBranchInherit(boolean isInherit, String projectId, String branchId);

    void setOrgPublic(boolean isPublic, String orgId);

    void setProjectPublic(boolean isPublic, String projectId);

    boolean hasOrgPrivilege(String privilege, String user, String orgId);

    boolean hasProjectPrivilege(String privilege, String user, String projectId);

    boolean hasBranchPrivilege(String privilege, String user, String projectId, String branchId);

    boolean isProjectInherit(String projectId);

    boolean isBranchInherit(String projectId, String branchId);

    boolean isOrgPublic(String orgId);

    boolean isProjectPublic(String projectId);
}
