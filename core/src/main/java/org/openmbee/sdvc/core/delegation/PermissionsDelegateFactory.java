package org.openmbee.sdvc.core.delegation;

import org.openmbee.sdvc.data.domains.global.Branch;
import org.openmbee.sdvc.data.domains.global.Organization;
import org.openmbee.sdvc.data.domains.global.Project;

public interface PermissionsDelegateFactory {

    PermissionsDelegate getPermissionsDelegate(Project project);

    PermissionsDelegate getPermissionsDelegate(Organization organization);

    PermissionsDelegate getPermissionsDelegate(Branch branch);
}
