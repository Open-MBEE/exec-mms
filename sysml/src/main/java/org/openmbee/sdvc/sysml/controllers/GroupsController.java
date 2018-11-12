package org.openmbee.sdvc.sysml.controllers;

import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.sysml.services.SysmlNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}/groups")
public class GroupsController extends BaseController {

    private SysmlNodeService nodeService;

    @Autowired
    public GroupsController(SysmlNodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping
    public ResponseEntity<?> handleGet(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam Map<String, String> params) {

        return ResponseEntity.ok(null);
    }
}
