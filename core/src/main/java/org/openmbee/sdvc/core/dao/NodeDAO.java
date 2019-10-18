package org.openmbee.sdvc.core.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.openmbee.sdvc.data.domains.scoped.Node;

public interface NodeDAO extends BaseDAO {

    public Node save(Node node);

    public List<Node> saveAll(List<Node> nodes);

    public List<Node> insertAll(List<Node> nodes);

    public List<Node> updateAll(List<Node> nodes);

    public void deleteAll(List<Node> nodes);

    public Optional<Node> findByNodeId(String nodeId);

    public List<Node> findAllByNodeIds(Collection<String> ids);

    public List<Node> findAll();

    public List<Node> findAllByDeleted(boolean deleted);

    public List<Node> findAllByDeletedAndNodeType(boolean deleted, int nodeType);
}
