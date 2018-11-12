package org.openmbee.sdvc.crud.controllers.projects;

import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.controllers.ErrorResponse;
import org.openmbee.sdvc.crud.services.ProjectService;
import org.openmbee.sdvc.crud.services.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class ProjectsController extends BaseController {

    private ServiceFactory serviceFactory;

    @Autowired
    public ProjectsController(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GetMapping(value = {"", "/{projectId}"})
    public ResponseEntity<?> handleGet(@PathVariable(required = false) String projectId) {
        if (projectId != null) {
            logger.debug("ProjectId given: ", projectId);
            return ResponseEntity.ok(projectId);
        } else {
            logger.debug("No ProjectId given");
            return ResponseEntity.ok("Requesting all projects");
        }
    }

    @PostMapping
    public ResponseEntity<? extends BaseResponse> handlePost(
        @RequestBody ProjectsRequest projectsPost) {
        if (!projectsPost.getProjects().isEmpty()) {
            ProjectsResponse response = serviceFactory.getProjectService("sysml").post(projectsPost);
            return ResponseEntity.ok(response);
        }
        logger.debug("Bad Request");
        ErrorResponse err = new ErrorResponse();
        err.setCode(400);
        err.setError("Bad Request");
        return ResponseEntity.badRequest().body(err);
    }

    @DeleteMapping(value = "/{projectId}")
    public ResponseEntity<? extends BaseResponse> handleDelete(@PathVariable String projectId) {
        return ResponseEntity.ok(new ProjectsResponse());
    }

    private ProjectService getProjectService(String projectId) {
        return serviceFactory.getProjectService("sysml");
    }
}
