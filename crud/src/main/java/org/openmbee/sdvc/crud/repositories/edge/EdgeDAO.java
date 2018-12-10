package org.openmbee.sdvc.crud.repositories.edge;

import java.util.List;
import org.openmbee.sdvc.crud.domains.Edge;
import org.openmbee.sdvc.crud.domains.EdgeType;

public interface EdgeDAO {

    public Edge save(Edge edge);

    public List<Edge> saveAll(List<Edge> nodes);

    public List<Edge> insertAll(List<Edge> nodes);

    public List<Edge> updateAll(List<Edge> nodes);

    public Edge findParents(String child, EdgeType et);

    public Edge findChildren(String parent, EdgeType et);

    public List<Edge> findAll();
}
