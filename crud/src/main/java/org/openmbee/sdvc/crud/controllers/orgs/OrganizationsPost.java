package org.openmbee.sdvc.crud.controllers.orgs;

import com.fasterxml.jackson.core.type.TypeReference;
import org.openmbee.sdvc.core.domains.Organization;
import org.openmbee.sdvc.core.repositories.OrganizationRepository;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.controllers.Constants;
import org.openmbee.sdvc.crud.controllers.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/orgs")
public class OrganizationsPost extends BaseController {

    @Autowired
    OrganizationRepository organizationRepository;

    @PostMapping
    public ResponseEntity<? extends BaseResponse> handleRequest(
        @RequestBody OrganizationsRequest orgPost) {

        if (!orgPost.getOrgs().isEmpty()) {
            logger.info("JSON parsed properly");
            logger.info(orgPost.getOrgs().get(0).getId());
            OrganizationsResponse response = new OrganizationsResponse();

            for (OrgJson org : orgPost.getOrgs()) {
                Organization o = new Organization();
                o.setOrganizationId(org.getId());
                o.setOrganizationName(org.getName());
                logger.info("Saving organization: {}", o.getOrganizationId());
                Organization saved = organizationRepository.save(o);
                response.getOrgs().add(org);
            }

            return ResponseEntity.ok(response);
        }
        logger.debug("Bad Request");
        ErrorResponse er = new ErrorResponse();
        er.setCode(400);
        er.setError("Bad Request");
        return ResponseEntity.badRequest().body(er);
    }
}
