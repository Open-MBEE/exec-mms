package org.openmbee.sdvc.crud.controllers.elements;

import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.repositories.node.NodeDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}/elements")
public class ElementsGet extends BaseController {

    private NodeDAOImpl nodeRepository;

    @Autowired
    public ElementsGet(NodeDAOImpl nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @GetMapping(value = {"", "/{elementId}"})
    public ResponseEntity<?> handleRequest(
        @PathVariable
            String projectId,
        @PathVariable
            String refId,
        @PathVariable(required = false)
            String elementId) {
        DbContextHolder.setContext(projectId, refId);
        if (elementId != null) {
            logger.debug("ElementId given: ", elementId);
            Node node = nodeRepository.findBySysmlId(elementId);
            return ResponseEntity.ok(new ElementsResponse(node));
        } else {
            logger.debug("No ElementId given");
            List<Node> nodes = nodeRepository.findAll();
            return ResponseEntity.ok(new ElementsResponse(nodes));
        }
    }
}
