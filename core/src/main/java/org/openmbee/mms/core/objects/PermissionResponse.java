package org.openmbee.mms.core.objects;

import java.util.ArrayList;
import java.util.List;

public class PermissionResponse {

    private List<Permission> permissions;

    public static class Permission {
        private String name;

        private String role;

        private boolean inherited;

        public Permission() {}
        public Permission(String name, String role, boolean inherited) {
            this.name = name;
            this.role = role;
            this.inherited = inherited;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public boolean isInherited() {
            return inherited;
        }

        public void setInherited(boolean inherited) {
            this.inherited = inherited;
        }
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public static PermissionResponse getDefaultResponse() {
        PermissionResponse res = new PermissionResponse();
        List<PermissionResponse.Permission> perms = new ArrayList<>();
        res.setPermissions(perms);
        return res;
    }
}
