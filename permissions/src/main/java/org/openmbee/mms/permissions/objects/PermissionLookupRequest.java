package org.openmbee.mms.permissions.objects;

import java.util.List;

public class PermissionLookupRequest {

    private List<PermissionLookup> lookups;

    public List<PermissionLookup> getLookups() {
        return lookups;
    }

    public void setLookups(List<PermissionLookup> lookups) {
        this.lookups = lookups;
    }
}
