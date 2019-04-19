package org.openmbee.sdvc.crud.repositories.edge;

import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.data.domains.Edge;

public interface EdgeDAO {

    Edge save(Edge edge);

    List<Edge> saveAll(List<Edge> nodes);

    List<Edge> insertAll(List<Edge> nodes);

    List<Edge> updateAll(List<Edge> nodes);

    Optional<Edge> findParents(String child, Integer et);

    Optional<Edge> findChildren(String parent, Integer et);

    List<Edge> findAll();
}
