package org.openmbee.sdvc.crud.repositories.node;

import java.util.List;
import org.openmbee.sdvc.crud.domains.Node;

public interface NodeDAO {

    public Node save(Node node);

    public Node findById(long id);

    public Node findBySysmlId(String sysmlid);

    public List<Node> findAll();
}
