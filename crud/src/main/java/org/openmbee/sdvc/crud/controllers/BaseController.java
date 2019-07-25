package org.openmbee.sdvc.crud.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.repositories.ProjectRepository;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;
import org.openmbee.sdvc.crud.exceptions.BadRequestException;
import org.openmbee.sdvc.crud.exceptions.DeletedException;
import org.openmbee.sdvc.crud.exceptions.NotFoundException;
import org.openmbee.sdvc.crud.exceptions.NotModifiedException;
import org.openmbee.sdvc.crud.services.NodeService;
import org.openmbee.sdvc.crud.services.ServiceFactory;
import org.openmbee.sdvc.data.domains.Project;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {

    protected final Logger logger = LogManager.getLogger(getClass());

    protected ObjectMapper om;

    protected ServiceFactory serviceFactory;

    protected ProjectRepository projectRepository;

    @Autowired
    public void setServiceFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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
            List<Map> rejected = res.getRejected();
            Integer code = (Integer) rejected.get(0).get("code");
            switch(code) {
                case 304:
                    throw new NotModifiedException(res);
                case 400:
                    throw new BadRequestException(res);
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
