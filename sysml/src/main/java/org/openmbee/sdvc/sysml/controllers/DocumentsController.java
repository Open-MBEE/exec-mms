package org.openmbee.sdvc.sysml.controllers;

import org.openmbee.sdvc.sysml.services.SysmlNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}/documents")
public class DocumentsController {

    private SysmlNodeService nodeService;

    @Autowired
    public DocumentsController(SysmlNodeService nodeService) {
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
