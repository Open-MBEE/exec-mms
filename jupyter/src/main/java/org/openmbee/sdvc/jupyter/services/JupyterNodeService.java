package org.openmbee.sdvc.jupyter.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.controllers.elements.ElementsRequest;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;
import org.openmbee.sdvc.crud.services.NodeChangeInfo;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.crud.services.DefaultNodeService;
import org.openmbee.sdvc.crud.services.NodeService;
import org.openmbee.sdvc.data.domains.Node;
import org.openmbee.sdvc.jupyter.JupyterConstants;
import org.openmbee.sdvc.jupyter.JupyterEdgeType;
import org.openmbee.sdvc.jupyter.JupyterNodeType;
import org.openmbee.sdvc.jupyter.controllers.NotebooksRequest;
import org.openmbee.sdvc.jupyter.controllers.NotebooksResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service("jupyterNodeService")
public class JupyterNodeService extends DefaultNodeService implements NodeService {

    private JupyterHelper jupyterHelper;

    @Autowired
    public void setJupyterHelper(JupyterHelper jupyterHelper) {
        this.jupyterHelper = jupyterHelper;
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

    public ElementsResponse readNotebooks(String projectId, String refId, String elementId,
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
        return readNotebooks(projectId, refId, req, params);
    }

    public ElementsResponse readNotebooks(String projectId, String refId, ElementsRequest req,
            Map<String, String> params) {
        ElementsResponse res = this.read(projectId, refId, req, new HashMap<>());
        for (ElementJson e: res.getElements()) {
            ElementsRequest req2 = new ElementsRequest();
            List<ElementJson> req2s = new ArrayList<>();
            if (!e.containsKey(JupyterConstants.CELLS) || e.get(JupyterConstants.CELLS) == null) {
                continue; //no cells; TODO should reject 400 as not a notebook
            }
            for (String cellId: (List<String>)e.get(JupyterConstants.CELLS)) { //stored notebooks have cells as list of ids, use cellIds?
                req2s.add((new ElementJson()).setId(cellId));
            }
            req2.setElements(req2s);
            ElementsResponse cells = this.read(projectId, refId, req2, params);
            e.put(JupyterConstants.CELLS, cells.getElements()); //TODO need to check cell order
        }
        return res;
    }

    public NotebooksResponse createOrUpdateNotebooks(String projectId, String refId, NotebooksRequest req,
            Map<String, String> params) {
        ElementsRequest req2 = new ElementsRequest();
        List<ElementJson> reqs = new ArrayList<>();
        for (ElementJson notebook: req.getNotebooks()) {
            List<String> cells = new ArrayList<>(); //to replace cells with list of cell ids
            for (Map<String, Object> cell: (List<Map<String, Object>>)notebook.get(JupyterConstants.CELLS)) {
                if (!cell.containsKey("id")) { //check metadata?
                    cell.put("id", UUID.randomUUID().toString());
                }
                cells.add((String)cell.get("id"));
                ElementJson postCell = new ElementJson();
                postCell.putAll(cell);
                reqs.add(postCell);
            }
            if (!notebook.containsKey("id")) {
                notebook.setId(UUID.randomUUID().toString());
            }
            ElementJson postNotebook = new ElementJson();
            postNotebook.putAll(notebook); //check for id?
            postNotebook.put(JupyterConstants.CELLS, cells); //either replace cells or remove cells and use cellIds
            reqs.add(postNotebook);
        }
        req2.setElements(reqs);
        ElementsResponse res = this.createOrUpdate(projectId, refId, req2, params);
        NotebooksResponse r = new NotebooksResponse();
        r.setNotebooks(res.getElements()); //TODO fix so returns original notebooks, with updated ids/cells
        r.setRejected(res.getRejected());
        return r;
    }
}
