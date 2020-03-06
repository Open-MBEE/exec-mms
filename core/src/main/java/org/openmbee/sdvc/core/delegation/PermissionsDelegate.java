package org.openmbee.sdvc.core.delegation;

import org.openmbee.sdvc.core.objects.PermissionResponse;
import org.openmbee.sdvc.core.objects.PermissionUpdateRequest;
import org.openmbee.sdvc.data.domains.global.User;

import java.util.Set;

public interface PermissionsDelegate {
    boolean hasPermission(String user, Set<String> groups, String privilege);
    void initializePermissions(String creator);
    void initializePermissions(String creator, boolean inherit);
    boolean setInherit(boolean isInherit);
    void setPublic(boolean isPublic);
    void updateUserPermissions(PermissionUpdateRequest req);
    void updateGroupPermissions(PermissionUpdateRequest req);
    PermissionResponse getUserRoles();
    PermissionResponse getGroupRoles();
    void recalculateInheritedPerms();
}
