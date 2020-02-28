package org.openmbee.sdvc.cameo.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmbee.sdvc.cameo.CameoConstants;
import org.openmbee.sdvc.cameo.CameoNodeType;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.config.Privileges;
import org.openmbee.sdvc.core.objects.ElementsRequest;
import org.openmbee.sdvc.core.security.MethodSecurityService;
import org.openmbee.sdvc.core.services.NodeChangeInfo;
import org.openmbee.sdvc.core.services.NodeGetInfo;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.crud.services.DefaultNodeService;
import org.openmbee.sdvc.core.services.NodeService;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.cameo.CameoEdgeType;
import org.openmbee.sdvc.json.MountJson;
import org.openmbee.sdvc.json.ProjectJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("cameoNodeService")
public class CameoNodeService extends DefaultNodeService implements NodeService {

    private CameoHelper cameoHelper;
    private MethodSecurityService mss;

    @Autowired
    public void setCameoHelper(CameoHelper cameoHelper) {
        this.cameoHelper = cameoHelper;
    }

    @Autowired
    public void setMss(MethodSecurityService mss) {
        this.mss = mss;
    }

    @Override
    public ElementsResponse read(String projectId, String refId, ElementsRequest req,
            Map<String, String> params) {

        String commitId = params.getOrDefault("commitId", null);
        ContextHolder.setContext(projectId, refId);
        NodeGetInfo info = nodeGetHelper.processGetJson(req.getElements(), commitId, this);

        if (!info.getRejected().isEmpty()) {
            //continue looking in visible mounted projects for elements if not all found
            NodeGetInfo curInfo = info;
            List<Pair<String, String>> usages = new ArrayList<>();
            getProjectUsages(projectId, refId, commitId, usages);

            int i = 1; //0 is entry project, already gotten
            while (!curInfo.getRejected().isEmpty() && i < usages.size()) {
                ElementsRequest reqNext = buildRequest(curInfo.getRejected().keySet());
                ContextHolder.setContext(usages.get(i).getFirst(), usages.get(i).getSecond());
                //TODO use the right commitId in child if commitId is present in params
                curInfo = nodeGetHelper.processGetJson(reqNext.getElements(), "", this);
                info.getActiveElementMap().putAll(curInfo.getActiveElementMap());
                curInfo.getActiveElementMap().forEach((id, json) -> info.getRejected().remove(id));
                curInfo.getRejected().forEach((id, rejection) -> {
                    if (info.getRejected().containsKey(id) && rejection.getCode() == 410) {
                        info.getRejected().put(id, rejection); //deleted element is better than not found
                    }
                });
                i++;
            }
        }

        ElementsResponse response = new ElementsResponse();
        response.getElements().addAll(info.getActiveElementMap().values());
        response.setRejected(new ArrayList<>(info.getRejected().values()));
        return response;
    }

    @Override
    public void extraProcessPostedElement(ElementJson element, Node node, NodeChangeInfo info) {
        node.setNodeType(cameoHelper.getNodeType(element).getValue());
        //TODO need to handle _childViews? need to remove it at minimum if posted
        List<Map<String, String>> postedChildViews = (List)element.remove(CameoConstants.CHILDVIEWS);
        //TODO move graph processing somewhere else/another interface
        Map<Integer, List<Pair<String, String>>> res = info.getEdgesToSave();
        String owner = (String) element.get("ownerId");
        if (owner != null && !owner.isEmpty()) {
            if (!res.containsKey(CameoEdgeType.CONTAINMENT.getValue())) {
                res.put(CameoEdgeType.CONTAINMENT.getValue(), new ArrayList<>());
            }
            res.get(CameoEdgeType.CONTAINMENT.getValue()).add(Pair.of(owner, element.getId()));
        }
    }

    @Override
    public void extraProcessGotElement(ElementJson element, Node node, NodeGetInfo info) {
        //TODO check if element is view, add in _childViews?
        //TODO extended info? (qualified name/id)
    }

    public MountJson getProjectUsages(String projectId, String refId, String commitId, List<Pair<String, String>> saw) {
        ContextHolder.setContext(projectId, refId);
        saw.add(Pair.of(projectId, refId));
        List<Node> mountNodes = nodeRepository.findAllByNodeType(CameoNodeType.PROJECTUSAGE.getValue());
        Set<String> mountIds = new HashSet<>();
        mountNodes.forEach(n -> mountIds.add(n.getNodeId()));
        Map<String, String> params = new HashMap<>();
        params.put("commitId", commitId);
        ElementsResponse mountsJson = super.read(projectId, refId, buildRequest(mountIds), params);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<MountJson> mountValues = new ArrayList<>();
        for (ElementJson mount: mountsJson.getElements()) {
            String mountedProjectId = (String)mount.get(CameoConstants.MOUNTEDELEMENTPROJECTID);
            String mountedRefId = (String)mount.get(CameoConstants.MOUNTEDREFID);
            if (saw.contains(Pair.of(mountedProjectId, mountedRefId))) {
                //prevent circular dependencies or dups - should it be by project or by project and ref?
                continue;
            }
            try {
                if (!mss.hasBranchPrivilege(auth, mountedProjectId, mountedRefId,
                    Privileges.BRANCH_READ.name(), true)) {
                    //should permission be considered here?
                    continue;
                }
            } catch (Exception e) {
                continue;
            }
            //doing a depth first traversal TODO get appropriate commitId
            mountValues.add(getProjectUsages(mountedProjectId, mountedRefId, "", saw));
        }
        MountJson res = new MountJson();
        res.setId(projectId);
        res.setProjectId(projectId);
        res.setRefId(refId);
        res.put(CameoConstants.MOUNTS, mountValues);
        return res;
    }
}
