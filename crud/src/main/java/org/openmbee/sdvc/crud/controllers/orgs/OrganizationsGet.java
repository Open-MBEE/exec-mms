package org.openmbee.sdvc.crud.controllers.orgs;

import org.openmbee.sdvc.crud.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orgs")
public class OrganizationsGet extends BaseController {

    @GetMapping(value = {"", "/{orgId}"})
    public ResponseEntity<?> handleRequest(@PathVariable(required = false) String orgId) {
        if (orgId != null) {
            logger.debug("OrgId given: ", orgId);
            return ResponseEntity.ok(orgId);
        } else {
            logger.debug("No OrgId given");
            return ResponseEntity.ok("Requesting all orgs");
        }
    }
}
