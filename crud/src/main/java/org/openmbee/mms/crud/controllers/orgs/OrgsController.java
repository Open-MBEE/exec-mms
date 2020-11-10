package org.openmbee.mms.crud.controllers.orgs;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import org.openmbee.mms.core.config.Privileges;
import org.openmbee.mms.core.dao.OrgDAO;
import org.openmbee.mms.core.objects.OrganizationsRequest;
import org.openmbee.mms.core.objects.OrganizationsResponse;
import org.openmbee.mms.core.objects.Rejection;
import org.openmbee.mms.data.domains.global.Organization;
import org.openmbee.mms.crud.controllers.BaseController;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.json.OrgJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orgs")
@Tag(name = "Orgs")
public class OrgsController extends BaseController {

    OrgDAO organizationRepository;

    @Autowired
    public OrgsController(OrgDAO organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @GetMapping
    @Transactional
    public OrganizationsResponse getAllOrgs( Authentication auth) {

        OrganizationsResponse response = new OrganizationsResponse();
        List<Organization> allOrgs = organizationRepository.findAll();
        for (Organization org : allOrgs) {
            if (mss.hasOrgPrivilege(auth, org.getOrganizationId(), Privileges.ORG_READ.name(), true)) {
                OrgJson orgJson = new OrgJson();
                orgJson.merge(convertToMap(org));
                response.getOrgs().add(orgJson);
            }
        }
        return response;
    }

    @GetMapping(value = "/{orgId}")
    @Transactional
    @PreAuthorize("@mss.hasOrgPrivilege(authentication, #orgId, 'ORG_READ', true)")
    public OrganizationsResponse getOrg(
        @PathVariable String orgId) {

        OrganizationsResponse response = new OrganizationsResponse();
        Optional<Organization> orgOption = organizationRepository.findByOrganizationId(orgId);
        if (!orgOption.isPresent()) {
            throw new NotFoundException(response.addMessage("Organization not found."));
        }
        OrgJson orgJson = new OrgJson();
        orgJson.merge(convertToMap(orgOption.get()));
        response.getOrgs().add(orgJson);
        return response;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public OrganizationsResponse createOrUpdateOrgs(
        @RequestBody OrganizationsRequest orgPost,
        Authentication auth) {

        OrganizationsResponse response = new OrganizationsResponse();
        if (orgPost.getOrgs().isEmpty()) {
            throw new BadRequestException(response.addMessage("No orgs provided"));
        }

        for (OrgJson org : orgPost.getOrgs()) {
            if (org.getId() == null || org.getId().isEmpty()) {
                org.setId(UUID.randomUUID().toString());
            }

            Organization o = organizationRepository.findByOrganizationId(org.getId())
                .orElse(new Organization());
            boolean newOrg = true;
            if (o.getId() != null) {
                if (!mss.hasOrgPrivilege(auth, o.getOrganizationId(), Privileges.ORG_EDIT.name(), false)) {
                    response.addRejection(new Rejection(org, 403, "No permission to update org"));
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
        return response;
    }

    @DeleteMapping(value = "/{orgId}")
    @Transactional
    @PreAuthorize("@mss.hasOrgPrivilege(authentication, #orgId, 'ORG_DELETE', false)")
    public OrganizationsResponse deleteOrg(
        @PathVariable String orgId) {

        OrganizationsResponse response = new OrganizationsResponse();
        Optional<Organization> orgOption = organizationRepository.findByOrganizationId(orgId);
        if (!orgOption.isPresent()) {
            throw new NotFoundException(response.addMessage("Organization not found."));
        }
        Organization org = orgOption.get();
        if (!org.getProjects().isEmpty()) {
            throw new BadRequestException(response.addMessage("Organization is not empty"));
        }
        organizationRepository.delete(org);
        return response;
    }
}
