package org.openmbee.sdvc.crud.controllers.orgs;

import java.util.List;
import javax.transaction.Transactional;
import org.openmbee.sdvc.core.domains.Organization;
import org.openmbee.sdvc.core.repositories.OrganizationRepository;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.json.OrgJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orgs")
public class OrgsController extends BaseController {

    OrganizationRepository organizationRepository;

    @Autowired
    public OrgsController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @GetMapping(value = {"", "/{orgId}"})
    @Transactional
    public ResponseEntity<?> handleGet(@PathVariable(required = false) String orgId) {
        OrganizationsResponse response = new OrganizationsResponse();

        if (orgId != null) {
            logger.debug("OrgId given: ", orgId);
            Organization org = organizationRepository.findByOrganizationId(orgId);
            OrgJson orgJson = new OrgJson();
            orgJson.merge(convertToMap(org));
            response.getOrgs().add(orgJson);
            return ResponseEntity.ok(response);
        } else {
            logger.debug("No OrgId given");
            List<Organization> allOrgs = organizationRepository.findAll();
            for (Organization org : allOrgs) {
                OrgJson orgJson = new OrgJson();
                orgJson.merge(convertToMap(org));
                response.getOrgs().add(orgJson);
            }
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<? extends BaseResponse> handlePost(
        @RequestBody OrganizationsRequest orgPost) {

        if (!orgPost.getOrgs().isEmpty()) {
            logger.info(orgPost.getOrgs().get(0).getId());
            OrganizationsResponse response = new OrganizationsResponse();

            for (OrgJson org : orgPost.getOrgs()) {
                Organization o = new Organization();
                o.setOrganizationId(org.getId());
                o.setOrganizationName(org.getName());
                logger.info("Saving organization: {}", o.getOrganizationId());
                Organization saved = organizationRepository.save(o);
                org.merge(convertToMap(saved));
                response.getOrgs().add(org);
            }

            return ResponseEntity.ok(response);
        } else {

        }
        logger.debug("Bad Request");
        OrganizationsResponse err = new OrganizationsResponse();
        err.setCode(400);
        err.addMessage("Bad Request");
        return ResponseEntity.badRequest().body(err);
    }
}
