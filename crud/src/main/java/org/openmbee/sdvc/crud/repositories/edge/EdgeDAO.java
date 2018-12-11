package org.openmbee.sdvc.crud.repositories.edge;

import java.util.List;
import org.openmbee.sdvc.crud.domains.Edge;
import org.openmbee.sdvc.crud.domains.EdgeType;

public interface EdgeDAO {

    Edge save(Edge edge);

    List<Edge> saveAll(List<Edge> nodes);

    List<Edge> insertAll(List<Edge> nodes);

    List<Edge> updateAll(List<Edge> nodes);

    Edge findParents(String child, Integer et);

    Edge findChildren(String parent, Integer et);

    List<Edge> findAll();
}
