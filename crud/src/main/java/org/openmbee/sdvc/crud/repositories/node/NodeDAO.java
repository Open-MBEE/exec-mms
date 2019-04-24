package org.openmbee.sdvc.crud.repositories.node;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.data.domains.Node;

public interface NodeDAO {

    public Node save(Node node);

    public List<Node> saveAll(List<Node> nodes);

    public List<Node> insertAll(List<Node> nodes);

    public List<Node> updateAll(List<Node> nodes);

    public Optional<Node> findById(long id);

    public Optional<Node> findByNodeId(String sysmlid);

    public List<Node> findAllByNodeIds(Collection<String> ids);

    public List<Node> findAll();

    public List<Node> findAllByDeleted(boolean deleted);
}
