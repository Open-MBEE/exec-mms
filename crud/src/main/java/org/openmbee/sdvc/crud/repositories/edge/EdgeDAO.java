package org.openmbee.sdvc.crud.repositories.edge;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.data.domains.Edge;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

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
