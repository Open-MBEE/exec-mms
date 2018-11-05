package org.openmbee.sdvc.crud.controllers.projects;

import org.openmbee.sdvc.crud.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectsGet extends BaseController {

    @GetMapping(value = {"", "/{projectId}"})
    public ResponseEntity<?> handleRequest(@PathVariable(required = false) String projectId) {
        if (projectId != null) {
            logger.debug("ProjectId given: ", projectId);
            return ResponseEntity.ok(projectId);
        } else {
            logger.debug("No ProjectId given");
            return ResponseEntity.ok("Requesting all projects");
        }
    }
}
