package org.openmbee.sdvc.crud.controllers.orgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;

import org.openmbee.sdvc.core.config.Privileges;
import org.openmbee.sdvc.core.objects.OrganizationsRequest;
import org.openmbee.sdvc.core.objects.OrganizationsResponse;
import org.openmbee.sdvc.core.security.CustomMSERoot;
import org.openmbee.sdvc.data.domains.global.Organization;
import org.openmbee.sdvc.rdb.repositories.OrganizationRepository;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.crud.exceptions.BadRequestException;
import org.openmbee.sdvc.crud.exceptions.NotFoundException;
import org.openmbee.sdvc.json.OrgJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orgs")
@Transactional
public class OrgsController extends BaseController {

    OrganizationRepository organizationRepository;

    @Autowired
    public OrgsController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @GetMapping(value = {"", "/{orgId}"})
    @PreAuthorize("#orgId == null || hasOrgPrivilege(#orgId, 'ORG_READ', true)")
    public ResponseEntity<?> handleGet(
        @PathVariable(required = false) String orgId,
        Authentication auth) {

        OrganizationsResponse response = new OrganizationsResponse();

        if (orgId != null) {
            logger.debug("OrgId given: ", orgId);
            Optional<Organization> orgOption = organizationRepository.findByOrganizationId(orgId);
            if (!orgOption.isPresent()) {
                throw new NotFoundException(response.addMessage("Organization not found."));
            }
            OrgJson orgJson = new OrgJson();
            orgJson.merge(convertToMap(orgOption.get()));
            response.getOrgs().add(orgJson);
        } else {
            logger.debug("No OrgId given");
            List<Organization> allOrgs = organizationRepository.findAll();
            for (Organization org : allOrgs) {
                if (permissionService.isOrgPublic(org.getOrganizationId()) ||
                    (!isAnonymous(auth) &&
                        permissionService.hasOrgPrivilege(Privileges.ORG_READ.name(), auth.getName(),
                            CustomMSERoot.getGroups(auth), org.getOrganizationId()))) {
                    OrgJson orgJson = new OrgJson();
                    orgJson.merge(convertToMap(org));
                    response.getOrgs().add(orgJson);
                }
            }
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<? extends BaseResponse> handlePost(
        @RequestBody OrganizationsRequest orgPost,
        Authentication auth) {

        OrganizationsResponse response = new OrganizationsResponse();
        if (orgPost.getOrgs().isEmpty()) {
            throw new BadRequestException(response.addMessage("No orgs provided"));
        }

        List<Map> rejected = new ArrayList<>();
        response.setRejected(rejected);

        for (OrgJson org : orgPost.getOrgs()) {
            if (org.getId() == null || org.getId().isEmpty()) {
                Map<String, Object> rejection = new HashMap<>();
                rejection.put("message", "Org id not provided");
                rejection.put("code", 400);
                rejection.put("org", org);
                rejected.add(rejection);
                continue;
            }
            Organization o = organizationRepository.findByOrganizationId(org.getId())
                .orElse(new Organization());
            boolean newOrg = true;
            if (o.getId() != null) {
                if (!permissionService.hasOrgPrivilege(Privileges.ORG_EDIT.name(), auth.getName(),
                        CustomMSERoot.getGroups(auth), o.getOrganizationId())) {
                    Map<String, Object> rejection = new HashMap<>();
                    rejection.put("message", "No permission to update org");
                    rejection.put("code", 403);
                    rejection.put("org", org);
                    rejected.add(rejection);
                    continue;
                }
                newOrg = false;
            }
            o.setOrganizationId(org.getId());
            o.setOrganizationName(org.getName());
            logger.info("Saving organization: {}", o.getOrganizationId());
            Organization saved = organizationRepository.save(o);
            if (newOrg) {
                permissionService.initOrgPerms(org.getId(), auth.getName());
            }
            org.merge(convertToMap(saved));
            response.getOrgs().add(org);
        }
        if (orgPost.getOrgs().size() == 1) {
            handleSingleResponse(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{orgId}")
    @PreAuthorize("hasOrgPrivilege(#orgId, 'ORG_DELETE', false)")
    public ResponseEntity<? extends BaseResponse> handleDelete(
        @PathVariable String orgId) {

        OrganizationsResponse response = new OrganizationsResponse();
        Optional<Organization> orgOption = organizationRepository.findByOrganizationId(orgId);
        if (!orgOption.isPresent()) {
            throw new NotFoundException(response.addMessage("Organization not found."));
        }

        return ResponseEntity.ok(new OrganizationsResponse()); //TODO
    }
}
