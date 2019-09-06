package org.openmbee.sdvc.crud.services;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmbee.sdvc.core.config.Formats;
import org.openmbee.sdvc.core.services.NodeChangeInfo;
import org.openmbee.sdvc.core.services.NodeService;
import org.openmbee.sdvc.json.BaseJson;
import org.openmbee.sdvc.json.CommitJson;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.data.domains.Node;
import org.springframework.stereotype.Service;

@Service
public class NodePostHelper extends NodeOperation {

    public boolean isUpdated(BaseJson element, Map<String, Object> existing,
                             Map<String, Object> rejection) {

        if (element.isPartialOf(existing)) {
            rejection.put("message", "Is Equivalent");
            rejection.put("code", 304);
            rejection.put("element", element);
            return false;
        }
        return true;
    }

    public boolean diffUpdateJson(BaseJson element, Map<String, Object> existing,
                                  Map<String, Object> rejection) {

        String jsonModified = element.getModified();
        Object existingModified = existing.get(BaseJson.MODIFIED);
        if (jsonModified != null && !jsonModified.isEmpty()) {
            try {
                Date jsonModDate = Formats.SDF.parse(jsonModified);
                Date existingModDate = Formats.SDF.parse(existingModified.toString());
                if (jsonModDate.before(existingModDate)) {
                    rejection.put("message", "Conflict Detected");
                    rejection.put("code", 409);
                    rejection.put("element", element);
                    return false;
                }
            } catch (ParseException e) {
                logger.info("date parse exception:" + jsonModified + " " + existingModified);
            }
        }
        element.merge(existing);
        return true;
    }

    // create new elastic id for all element json, update modified time, modifier (use dummy for now), set _projectId, _refId, _inRefIds
    public NodeChangeInfo processPostJson(List<ElementJson> elements, boolean overwriteJson,
                                          CommitJson cmjs, NodeService service) {

        NodeChangeInfo info = initInfo(elements, cmjs);

        // Logic for update/add
        for (ElementJson element : elements) {
            if (element == null) {
                continue;
            }
            Map<String, Object> rejected = new HashMap<>();
            boolean added = false;
            boolean updated = false;
            if (element.getId() == null || element.getId().isEmpty()) {
                rejected.put("message", "Missing ID");
                rejected.put("code", 400);
                rejected.put("element", element);
            } else {
                Map<String, Object> elasticElement = info.getExistingElementMap()
                    .get(element.getId());
                Node n = info.getExistingNodeMap().get(element.getId());
                if (n == null) {
                    added = true;
                } else if (elasticElement == null) {
                    continue; //TODO this should be an error - the db has entry but elastic doesn't, reject 500?
                }

                if (!added) {
                    if (!overwriteJson) {
                        if (n.isDeleted() || isUpdated(element, elasticElement, rejected)) {
                            updated = diffUpdateJson(element, elasticElement, rejected);
                        }
                    } else {
                        updated = true;
                    }
                }
            }

            // create new elastic id for all element json, update modified time, modifier (use dummy for now), set _projectId, _refId, _inRefIds
            if (added) {
                Node node = new Node();
                processElementAdded(element, node, info.getCommitJson());
                info.getToSaveNodeMap().put(node.getNodeId(), node);
                info.getUpdatedMap().put(element.getId(), element);
                service.extraProcessPostedElement(element, node, info);
            } else if (updated) {
                Node node = info.getExistingNodeMap().get(element.getId());
                info.getOldDocIds().add(node.getDocId());
                processElementUpdated(element, node, info.getCommitJson());
                info.getToSaveNodeMap().put(node.getNodeId(), node);
                info.getUpdatedMap().put(element.getId(), element);
                service.extraProcessPostedElement(element, node, info);
            } else {
                info.getRejected().add(rejected);
            }
        }
        return info;
    }
}
