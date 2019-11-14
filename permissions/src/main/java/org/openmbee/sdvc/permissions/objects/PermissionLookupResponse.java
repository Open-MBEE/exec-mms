package org.openmbee.sdvc.permissions.objects;

import java.util.List;
import java.util.Map;

public class PermissionLookupResponse {

    private List<PermissionLookup> lookups;

    private List<Map> rejected;

    private boolean allPassed;

    public boolean isAllPassed() {
        return allPassed;
    }

    public void setAllPassed(boolean allPassed) {
        this.allPassed = allPassed;
    }

    public List<PermissionLookup> getLookups() {
        return lookups;
    }

    public void setLookups(List<PermissionLookup> lookups) {
        this.lookups = lookups;
    }

    public List<Map> getRejected() {
        return rejected;
    }

    public void setRejected(List<Map> rejected) {
        this.rejected = rejected;
    }
}
