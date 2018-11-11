package org.openmbee.sdvc.crud.repositories.node;

import java.util.List;
import org.openmbee.sdvc.crud.domains.Node;

public interface NodeDAO {

    public Node save(Node node);

    public List<Node> saveAll(List<Node> nodes);

    public Node findById(long id);

    public Node findBySysmlId(String sysmlid);

    public List<Node> findAllBySysmlIds(List<String> ids);

    public List<Node> findAll();
}
