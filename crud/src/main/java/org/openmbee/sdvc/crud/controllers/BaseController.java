package org.openmbee.sdvc.crud.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.dao.ProjectDAO;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.core.objects.Rejection;
import org.openmbee.sdvc.core.security.MethodSecurityService;
import org.openmbee.sdvc.core.services.PermissionService;
import org.openmbee.sdvc.core.exceptions.ForbiddenException;
import org.openmbee.sdvc.core.exceptions.UnauthorizedException;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.exceptions.DeletedException;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.exceptions.NotModifiedException;
import org.openmbee.sdvc.core.services.NodeService;
import org.openmbee.sdvc.crud.services.ServiceFactory;
import org.openmbee.sdvc.data.domains.global.Project;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {

    protected final Logger logger = LogManager.getLogger(getClass());

    protected ObjectMapper om;

    protected ServiceFactory serviceFactory;

    protected ProjectDAO projectRepository;

    protected PermissionService permissionService;

    protected MethodSecurityService mss;

    @Autowired
    public void setServiceFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Autowired
    public void setProjectRepository(ProjectDAO projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Autowired
    public void setMss(MethodSecurityService mss) {
        this.mss = mss;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper om) {
        this.om = om;
    }

    public Map<String, Object> convertToMap(Object obj) {
        return om.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }

    protected String getProjectType(String projectId) {
        Optional<Project> p = projectRepository.findByProjectId(projectId);
        if (p.isPresent()) {
            return p.get().getProjectType();
        }
        throw new NotFoundException("project not found");
    }

    protected NodeService getNodeService(String projectId) {
        return serviceFactory.getNodeService(this.getProjectType(projectId));
    }

    protected void handleSingleResponse(BaseResponse res) {
        if (res.getRejected() != null && !res.getRejected().isEmpty()) {
            List<Rejection> rejected = res.getRejected();
            int code = rejected.get(0).getCode();
            switch(code) {
                case 304:
                    throw new NotModifiedException(res);
                case 400:
                    throw new BadRequestException(res);
                case 401:
                    throw new UnauthorizedException(res);
                case 403:
                    throw new ForbiddenException(res);
                case 404:
                    throw new NotFoundException(res);
                case 410:
                    throw new DeletedException(res);
                default:
                    break;
            }
        }
    }
}
