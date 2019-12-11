package org.openmbee.sdvc.permissions.objects;

import java.util.List;
import org.openmbee.sdvc.core.objects.BaseResponse;

public class PermissionLookupResponse extends BaseResponse<PermissionLookupResponse> {

    private List<PermissionLookup> lookups;

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

}
