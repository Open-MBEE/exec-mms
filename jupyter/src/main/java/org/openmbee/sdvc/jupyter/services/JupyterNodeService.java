package org.openmbee.sdvc.jupyter.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openmbee.sdvc.core.config.DbContextHolder;
import org.openmbee.sdvc.core.objects.ElementsRequest;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.core.services.NodeChangeInfo;
import org.openmbee.sdvc.crud.services.NodeOperation;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.core.services.NodeService;
import org.openmbee.sdvc.data.domains.Node;
import org.openmbee.sdvc.jupyter.JupyterConstants;
import org.openmbee.sdvc.jupyter.JupyterEdgeType;
import org.openmbee.sdvc.jupyter.JupyterNodeType;
import org.openmbee.sdvc.jupyter.controllers.NotebooksRequest;
import org.openmbee.sdvc.jupyter.controllers.NotebooksResponse;
import org.openmbee.sdvc.rdb.repositories.node.NodeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service("jupyterNodeService")
public class JupyterNodeService implements NodeService {

    private JupyterHelper jupyterHelper;
    protected NodeDAO nodeRepository;

    @Autowired
    public void setJupyterHelper(JupyterHelper jupyterHelper) {
        this.jupyterHelper = jupyterHelper;
    }

    @Autowired
    public void setNodeRepository(NodeDAO nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Override
    public void extraProcessPostedElement(ElementJson element, Node node, NodeChangeInfo info) {
        node.setNodeType(JupyterHelper.getNodeType(element).getValue());
        Map<Integer, List<Pair<String, String>>> res = info.getEdgesToSave();
        List<String> cells = (List<String>) element.get(JupyterConstants.CELLS); //check list of cell ids
        if (cells != null && !cells.isEmpty()) {
            if (!res.containsKey(JupyterEdgeType.CONTAINMENT.getValue())) {
                res.put(JupyterEdgeType.CONTAINMENT.getValue(), new ArrayList<>());
            }
            for (String cellId: cells) {
                res.get(JupyterEdgeType.CONTAINMENT.getValue()).add(Pair.of(element.getId(), cellId));
            }
        }
    }

    @Override
    public void extraProcessDeletedElement(ElementJson element, Node node, NodeChangeInfo info) {
    }

    public ElementsResponse read(String projectId, String refId, String elementId,
            Map<String, String> params) {
        ElementsRequest req = new ElementsRequest();
        List<ElementJson> reqs = new ArrayList<>();
        if (elementId == null) {
            DbContextHolder.setContext(projectId, refId);
            List<Node> notebooks = this.nodeRepository
                .findAllByDeletedAndNodeType(false, JupyterNodeType.NOTEBOOK.getValue());
            for (Node n : notebooks) {
                reqs.add((new ElementJson()).setId(n.getNodeId()));
            }
        } else {
            reqs.add((new ElementJson()).setId(elementId));
        }
        req.setElements(reqs);
        return read(projectId, refId, req, params);
    }

    public ElementsResponse read(String projectId, String refId, ElementsRequest req,
            Map<String, String> params) {
        ElementsResponse res = this.read(projectId, refId, req, new HashMap<>());
        List<Map> rejected = new ArrayList<>(res.getRejected());
        List<ElementJson> nonNotebooks = new ArrayList<>();
        for (ElementJson e: res.getElements()) {
            List<ElementJson> req2s = new ArrayList<>();
            if (!e.containsKey(JupyterConstants.CELLS) || e.get(JupyterConstants.CELLS) == null) {
                Map<String, Object> reject = new HashMap<>();
                reject.put("code", 400);
                reject.put("message", "not a notebook");
                reject.put("id", e.getId());
                reject.put("element", e);
                rejected.add(reject);
                nonNotebooks.add(e);
                continue;
            }
            for (String cellId: (List<String>)e.get(JupyterConstants.CELLS)) { //stored notebooks have cells as list of ids, use cellIds?
                req2s.add((new ElementJson()).setId(cellId));
            }
            ElementsRequest req2 = new ElementsRequest();
            req2.setElements(req2s);
            ElementsResponse cells = this.read(projectId, refId, req2, params);
            Map<String, ElementJson> cellmap = NodeOperation.convertJsonToMap(cells.getElements());
            e.put(JupyterConstants.CELLS, order((List<String>)e.get(JupyterConstants.CELLS), cellmap));
        }
        res.getElements().removeAll(nonNotebooks);
        res.setRejected(rejected);
        return res;
    }

    public NotebooksResponse createOrUpdate(String projectId, String refId, NotebooksRequest req,
            Map<String, String> params) {
        List<ElementJson> postReqs = new ArrayList<>();
        List<ElementJson> resReqs = new ArrayList<>();
        for (ElementJson notebook: req.getNotebooks()) {
            List<String> cells = new ArrayList<>(); //to replace cells with list of cell ids
            for (Map<String, Object> cell: (List<Map<String, Object>>)notebook.get(JupyterConstants.CELLS)) {
                if (!cell.containsKey("id")) { //check metadata?
                    cell.put("id", UUID.randomUUID().toString());
                }
                cells.add((String)cell.get("id"));
                ElementJson postCell = new ElementJson();
                postCell.putAll(cell);
                postReqs.add(postCell);
            }
            if (!notebook.containsKey("id")) {
                notebook.setId(UUID.randomUUID().toString());
            }
            resReqs.add((new ElementJson()).setId(notebook.getId()));
            ElementJson postNotebook = new ElementJson();
            postNotebook.putAll(notebook);
            postNotebook.put(JupyterConstants.CELLS, cells); //either replace cells or remove cells and use cellIds
            postReqs.add(postNotebook);
        }
        ElementsRequest postReq = new ElementsRequest();
        postReq.setElements(postReqs);
        this.createOrUpdate(projectId, refId, postReq, params);

        ElementsRequest resReq = new ElementsRequest();
        resReq.setElements(resReqs);
        ElementsResponse res = this.read(projectId, refId, resReq, params);
        NotebooksResponse r = new NotebooksResponse();
        r.setNotebooks(res.getElements());
        return r;
    }

    protected List<ElementJson> order(List<String> ids, Map<String, ElementJson> map) {
        List<ElementJson> res = new ArrayList<>();
        for (String id: ids) {
            res.add(map.get(id));
        }
        return res;
    }

    @Override
    public ElementsResponse delete(String projectId, String refId, String id) {
        ElementsRequest req = buildRequest(id);
        return delete(projectId, refId, req);
    }

    protected ElementsRequest buildRequest(String id) {
        ElementJson json = new ElementJson();
        json.setId(id);
        ElementsRequest req = new ElementsRequest();
        List<ElementJson> list = new ArrayList<>();
        list.add(json);
        req.setElements(list);
        return req;
    }

    @Override
    public ElementsResponse delete(String projectId, String refId, ElementsRequest req) {
        ElementsResponse response = new ElementsResponse();
        return response;
    }

    @Override
    public ElementsResponse createOrUpdate(String projectId, String refId, ElementsRequest req,
                                           Map<String, String> params) {
        ElementsResponse response = new ElementsResponse();
        return response;
    }
}
