package org.openmbee.sdvc.rdb.repositories.node;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.openmbee.sdvc.rdb.repositories.BaseDAO;
import org.openmbee.sdvc.data.domains.Node;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

public interface NodeDAO extends BaseDAO {

    public Node save(Node node) throws InvalidDataAccessApiUsageException, DataRetrievalFailureException;

    public List<Node> saveAll(List<Node> nodes) throws SQLException;

    public List<Node> insertAll(List<Node> nodes) throws SQLException;

    public List<Node> updateAll(List<Node> nodes) throws SQLException;

    public void deleteAll(List<Node> nodes) throws SQLException;

    public Optional<Node> findById(long id);

    public Optional<Node> findByNodeId(String nodeId);

    public List<Node> findAllByNodeIds(Collection<String> ids);

    public List<Node> findAll();

    public List<Node> findAllByDeleted(boolean deleted);

    public List<Node> findAllByDeletedAndNodeType(boolean deleted, int nodeType);
}
