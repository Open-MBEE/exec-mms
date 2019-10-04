package org.openmbee.sdvc.core.services;

import java.util.Set;
import org.openmbee.sdvc.core.objects.PermissionResponse;
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

    boolean hasOrgPrivilege(String privilege, String user, Set<String> groups, String orgId);

    boolean hasProjectPrivilege(String privilege, String user, Set<String> groups, String projectId);

    boolean hasBranchPrivilege(String privilege, String user, Set<String> groups, String projectId, String branchId);

    boolean isProjectInherit(String projectId);

    boolean isBranchInherit(String projectId, String branchId);

    boolean isOrgPublic(String orgId);

    boolean isProjectPublic(String projectId);

    PermissionResponse getOrgGroupRoles(String orgId);

    PermissionResponse getOrgUserRoles(String orgId);

    PermissionResponse getProjectGroupRoles(String projectId);

    PermissionResponse getProjectUserRoles(String projectId);

    PermissionResponse getBranchGroupRoles(String projectId, String branchId);

    PermissionResponse getBranchUserRoles(String projectId, String branchId);
}
