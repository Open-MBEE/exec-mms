package org.openmbee.mms.core.builders;

import org.openmbee.mms.core.objects.PermissionUpdateResponse;
import org.openmbee.mms.data.domains.global.BranchGroupPerm;
import org.openmbee.mms.data.domains.global.BranchUserPerm;
import org.openmbee.mms.data.domains.global.OrgGroupPerm;
import org.openmbee.mms.data.domains.global.OrgUserPerm;
import org.openmbee.mms.data.domains.global.ProjectGroupPerm;
import org.openmbee.mms.data.domains.global.ProjectUserPerm;

import java.util.*;

public class PermissionUpdateResponseBuilder {

    private class PermissionUpdateWrapper {

        private PermissionUpdateResponse.PermissionUpdate permissionUpdate;

        public PermissionUpdateWrapper(PermissionUpdateResponse.PermissionUpdate permissionUpdate) {
            this.permissionUpdate = permissionUpdate;
        }

        public PermissionUpdateResponse.PermissionUpdate getPermissionUpdate() {
            return permissionUpdate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PermissionUpdateWrapper that = (PermissionUpdateWrapper) o;
            return Objects.equals(getPermissionUpdate().getBranchId(), that.getPermissionUpdate().getBranchId())
                && Objects.equals(getPermissionUpdate().getProjectId(), that.getPermissionUpdate().getProjectId())
                && Objects.equals(getPermissionUpdate().getOrgId(), that.getPermissionUpdate().getOrgId())
                && Objects.equals(getPermissionUpdate().getName(), that.getPermissionUpdate().getName())
                && Objects.equals(getPermissionUpdate().isInherited(), that.getPermissionUpdate().isInherited());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getPermissionUpdate().getBranchId(), getPermissionUpdate().getProjectId(),
                getPermissionUpdate().getOrgId(), getPermissionUpdate().getName(), getPermissionUpdate().isInherited());
        }
    }

    private HashSet<PermissionUpdateWrapper> removed = new HashSet<>();
    private HashSet<PermissionUpdateWrapper> added = new HashSet<>();

    public PermissionUpdateResponseBuilder insert(PermissionUpdateResponse updateUserPermissions) {
        updateUserPermissions.getPermissionUpdates().forEach(v -> doInsert(v));
        return this;
    }

    public void insertPermissionUpdates_OrgUserPerm(PermissionUpdateResponse.Action action, Collection<OrgUserPerm> perms) {
        perms.forEach(v -> insertPermissionUpdate(action, v));
    }

    public void insertPermissionUpdate(PermissionUpdateResponse.Action action, OrgUserPerm v) {
        if(v == null)
            return;

        PermissionUpdateResponse.PermissionUpdate update = new PermissionUpdateResponse.PermissionUpdate(
            action, v.getUser().getUsername(), v.getRole().getName(), v.getOrganization().getOrganizationId(), v.getOrganization().getOrganizationName(),
            null, null, null, false);
        doInsert(update);
    }

    public void insertPermissionUpdates_OrgGroupPerm(PermissionUpdateResponse.Action action, Collection<OrgGroupPerm> perms) {
        perms.forEach(v -> insertPermissionUpdate(action, v));
    }

    public void insertPermissionUpdate(PermissionUpdateResponse.Action action, OrgGroupPerm v) {
        if(v == null)
            return;

        PermissionUpdateResponse.PermissionUpdate update = new PermissionUpdateResponse.PermissionUpdate(
            action, v.getGroup().getName(), v.getRole().getName(), v.getOrganization().getOrganizationId(), v.getOrganization().getOrganizationName(),
            null, null, null, false);
        doInsert(update);
    }

    public void insertPermissionUpdates_ProjectUserPerm(PermissionUpdateResponse.Action action, Collection<ProjectUserPerm> perms) {
        perms.forEach(v -> insertPermissionUpdate(action, v));
    }

    public void insertPermissionUpdate(PermissionUpdateResponse.Action action, ProjectUserPerm v) {
        if(v == null)
            return;

        PermissionUpdateResponse.PermissionUpdate update = new PermissionUpdateResponse.PermissionUpdate(
            action, v.getUser().getUsername(), v.getRole().getName(), v.getProject().getOrganization().getOrganizationId(),
            v.getProject().getOrganization().getOrganizationName(), v.getProject().getProjectId(), v.getProject().getProjectName(),
            null, v.isInherited());
        doInsert(update);
    }

    public void insertPermissionUpdates_ProjectGroupPerm(PermissionUpdateResponse.Action action, Collection<ProjectGroupPerm> perms) {
        perms.forEach(v -> insertPermissionUpdate(action, v));
    }

    public void insertPermissionUpdate(PermissionUpdateResponse.Action action, ProjectGroupPerm v) {
        if(v == null)
            return;

        PermissionUpdateResponse.PermissionUpdate update = new PermissionUpdateResponse.PermissionUpdate(
            action, v.getGroup().getName(), v.getRole().getName(), v.getProject().getOrganization().getOrganizationId(),
            v.getProject().getOrganization().getOrganizationName(), v.getProject().getProjectId(), v.getProject().getProjectName(),
            null, v.isInherited());
        doInsert(update);
    }

    public void insertPermissionUpdates_BranchUserPerm(PermissionUpdateResponse.Action action, Collection<BranchUserPerm> perms) {
        perms.forEach(v -> insertPermissionUpdate(action, v));
    }

    public void insertPermissionUpdate(PermissionUpdateResponse.Action action, BranchUserPerm v) {
        if(v == null)
            return;

        PermissionUpdateResponse.PermissionUpdate update = new PermissionUpdateResponse.PermissionUpdate(
            action, v.getUser().getUsername(), v.getRole().getName(), v.getBranch().getProject().getOrganization().getOrganizationId(),
            v.getBranch().getProject().getOrganization().getOrganizationName(), v.getBranch().getProject().getProjectId(),
            v.getBranch().getProject().getProjectName(), v.getBranch().getBranchId(), v.isInherited());
        doInsert(update);
    }

    public void insertPermissionUpdates_BranchGroupPerm(PermissionUpdateResponse.Action action, Collection<BranchGroupPerm> perms) {
        perms.forEach(v -> insertPermissionUpdate(action, v));
    }

    public void insertPermissionUpdate(PermissionUpdateResponse.Action action, BranchGroupPerm v) {
        if(v == null)
            return;

        PermissionUpdateResponse.PermissionUpdate update = new PermissionUpdateResponse.PermissionUpdate(
            action, v.getGroup().getName(), v.getRole().getName(), v.getBranch().getProject().getOrganization().getOrganizationId(),
            v.getBranch().getProject().getOrganization().getOrganizationName(), v.getBranch().getProject().getProjectId(),
            v.getBranch().getProject().getProjectName(),v.getBranch().getBranchId(), v.isInherited());
        doInsert(update);
    }

    public PermissionUpdateResponse getPermissionUpdateResponse() {
        PermissionUpdateResponse response = new PermissionUpdateResponse();
        ArrayList<PermissionUpdateResponse.PermissionUpdate> updates = new ArrayList<>(removed.size() + added.size());
        removed.stream().map(PermissionUpdateWrapper::getPermissionUpdate).forEach(v -> updates.add(v));
        added.stream().map(PermissionUpdateWrapper::getPermissionUpdate).forEach(v -> updates.add(v));
        response.setPermissionUpdates(updates);
        return response;
    }

    private void doInsert(PermissionUpdateResponse.PermissionUpdate update) {
        PermissionUpdateWrapper wrapped = new PermissionUpdateWrapper(update);
        if(update.getAction() == PermissionUpdateResponse.Action.ADD) {
            if(! removed.remove(wrapped)) {
                added.add(wrapped);
            }
        } else if(! added.remove(wrapped)){
            removed.add(wrapped);
        }
    }
}
