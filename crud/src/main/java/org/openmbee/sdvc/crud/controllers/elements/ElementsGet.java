package org.openmbee.sdvc.crud.controllers.elements;

import java.util.List;
import java.util.Map;

import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.repositories.node.NodeDAOImpl;
import org.openmbee.sdvc.crud.services.NodeService;
import org.openmbee.sdvc.crud.services.NodeServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}/elements")
public class ElementsGet extends BaseController {

    private NodeServiceFactory nodeServiceFactory;

    @Autowired
    public ElementsGet(NodeServiceFactory nodeServiceFactory) {
        this.nodeServiceFactory = nodeServiceFactory;
    }

    @GetMapping(value = {"", "/{elementId}"})
    public ResponseEntity<?> handleRequest(
        @PathVariable
            String projectId,
        @PathVariable
            String refId,
        @PathVariable(required = false)
            String elementId,
        @RequestParam Map<String, String> params) {

        String type = "sysml";
        NodeService nodeService = nodeServiceFactory.getNodeService(type);
        ElementsResponse res = nodeService.get(projectId, refId, elementId, params);
        return ResponseEntity.ok(res);
        /*DbContextHolder.setContext(projectId, refId);
        if (elementId != null) {
            logger.debug("ElementId given: ", elementId);
            Node node = nodeRepository.findBySysmlId(elementId);
            return ResponseEntity.ok(new ElementsResponse(node));
        } else {
            logger.debug("No ElementId given");
            List<Node> nodes = nodeRepository.findAll();
            return ResponseEntity.ok(new ElementsResponse(nodes));
        }*/
    }
}
