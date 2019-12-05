package org.openmbee.sdvc.permissions.objects;

import java.util.List;
import java.util.Map;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.core.objects.Rejection;

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
