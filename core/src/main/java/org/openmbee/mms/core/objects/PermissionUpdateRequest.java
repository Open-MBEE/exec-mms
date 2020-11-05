package org.openmbee.mms.core.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class PermissionUpdateRequest {

    public enum Action {MODIFY, REPLACE, REMOVE;}

    public static class Permission {
        private String name;

        private String role;

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
    }

    @Schema(required = true)
    private Action action;

    @Schema(required = true)
    private List<Permission> permissions;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
