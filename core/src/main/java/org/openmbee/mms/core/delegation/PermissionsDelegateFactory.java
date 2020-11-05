package org.openmbee.mms.core.delegation;

import org.openmbee.mms.data.domains.global.Branch;
import org.openmbee.mms.data.domains.global.Organization;
import org.openmbee.mms.data.domains.global.Project;

public interface PermissionsDelegateFactory {

    PermissionsDelegate getPermissionsDelegate(Project project);

    PermissionsDelegate getPermissionsDelegate(Organization organization);

    PermissionsDelegate getPermissionsDelegate(Branch branch);
}
