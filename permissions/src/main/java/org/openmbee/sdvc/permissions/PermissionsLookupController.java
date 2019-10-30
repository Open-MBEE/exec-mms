package org.openmbee.sdvc.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmbee.sdvc.core.security.MethodSecurityService;
import org.openmbee.sdvc.permissions.exceptions.PermissionException;
import org.openmbee.sdvc.permissions.objects.PermissionLookup;
import org.openmbee.sdvc.permissions.objects.PermissionLookupRequest;
import org.openmbee.sdvc.permissions.objects.PermissionLookupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PermissionsLookupController {

    MethodSecurityService mss;

    @Autowired
    public PermissionsLookupController(MethodSecurityService mss) {
        this.mss = mss;
    }

    @PostMapping(value = "/permissions")
    public PermissionLookupResponse lookup(@RequestBody PermissionLookupRequest req, Authentication auth) {
        PermissionLookupResponse res = new PermissionLookupResponse();
        List<PermissionLookup> lookups = new ArrayList<>();
        List<Map> rejected = new ArrayList<>();
        res.setLookups(lookups);
        res.setRejected(rejected);
        for (PermissionLookup lookup: req.getLookups()) {
            try {
                boolean result;
                String pri = lookup.getPrivilege().name();
                boolean anon = lookup.isAllowAnonIfPublic();
                switch(lookup.getType()) {
                    case ORG:
                        result = mss.hasOrgPrivilege(auth, lookup.getOrgId(), pri, anon);
                        break;
                    case PROJECT:
                        result = mss.hasProjectPrivilege(auth, lookup.getProjectId(), pri, anon);
                        break;
                    case BRANCH:
                        result = mss.hasBranchPrivilege(auth, lookup.getProjectId(), lookup.getRefId(), pri, anon);
                        break;
                    default:
                        result = false;
                }
                lookup.setHasPrivilege(result);
                lookups.add(lookup);
            } catch (PermissionException e) {
                Map<String, Object> map = new HashMap<>();
                map.put("code", e.getCode().value());
                map.put("message", e.getMessage());
                map.put("lookup", lookup);
                rejected.add(map);
            }
        }
        return res;
    }
}
