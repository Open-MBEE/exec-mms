package org.openmbee.sdvc.permissions.delegation;

import org.openmbee.sdvc.core.delegation.PermissionsDelegate;
import org.openmbee.sdvc.core.delegation.PermissionsDelegateFactory;
import org.openmbee.sdvc.data.domains.global.Branch;
import org.openmbee.sdvc.data.domains.global.Organization;
import org.openmbee.sdvc.data.domains.global.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class DefaultPermissionsDelegateFactory implements PermissionsDelegateFactory {

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public PermissionsDelegate getPermissionsDelegate(Project project) {
        return autowire(new DefaultProjectPermissionsDelegate(project));
    }

    @Override
    public PermissionsDelegate getPermissionsDelegate(Organization organization) {
        return autowire(new DefaultOrgPermissionsDelegate(organization));
    }

    @Override
    public PermissionsDelegate getPermissionsDelegate(Branch branch) {
        return autowire(new DefaultBranchPermissionsDelegate(branch));
    }

    private PermissionsDelegate autowire(PermissionsDelegate permissionsDelegate) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(permissionsDelegate);
        return permissionsDelegate;
    }

}
