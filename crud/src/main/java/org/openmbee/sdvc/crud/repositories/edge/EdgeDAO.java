package org.openmbee.sdvc.crud.repositories.edge;

import java.util.List;
import org.openmbee.sdvc.crud.domains.Edge;
import org.openmbee.sdvc.crud.domains.EdgeType;

public interface EdgeDAO {

    public Edge save(Edge edge);

    public Edge findParents(String child, EdgeType et);

    public Edge findChildren(String parent, EdgeType et);

    public List<Edge> findAll();
}
