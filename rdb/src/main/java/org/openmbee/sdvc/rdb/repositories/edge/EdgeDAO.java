package org.openmbee.sdvc.rdb.repositories.edge;

import java.sql.SQLException;
import java.util.List;

import org.openmbee.sdvc.data.domains.scoped.Edge;

public interface EdgeDAO {

    Edge save(Edge edge);

    List<Edge> saveAll(List<Edge> edges) throws SQLException;

    List<Edge> insertAll(List<Edge> edges) throws SQLException;

    List<Edge> updateAll(List<Edge> edges) throws SQLException;

    void deleteAll(List<Edge> edges) throws SQLException;

    List<Edge> findParents(String child, Integer et);

    List<Edge> findChildren(String parent, Integer et);

    List<Edge> findAll();
}
