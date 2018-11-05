package org.openmbee.sdvc.crud.repositories.node;

import static org.openmbee.sdvc.crud.config.ContextObject.MASTER_BRANCH;

import java.sql.Timestamp;
import java.util.List;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.repositories.BaseDAOImpl;

public class NodeDAOImpl extends BaseDAOImpl implements NodeDAO {

    public Node save(Node node) {
        String refId = DbContextHolder.getContext().getBranchId();
        String sql = String.format(
            "INSERT INTO nodes%s (sysmlid, elasticid, lastcommit, initialcommit, deleted, created, createdby, modified, modifiedby) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
            !refId.equalsIgnoreCase(MASTER_BRANCH) ? "_" + refId : "");

        getConnection().update(sql,
            node.getSysmlId(),
            node.getElasticId(),
            node.getLastCommit(),
            node.getInitialCommit(),
            node.isDeleted(),
            Timestamp.from(node.getCreated()),
            node.getCreatedBy(),
            Timestamp.from(node.getModified()),
            node.getModifiedBy()
        );

        return node;
    }

    @SuppressWarnings({"unchecked"})
    public Node findBySysmlId(String sysmlId) {
        String refId = DbContextHolder.getContext().getBranchId();
        String sql = String.format("SELECT * FROM nodes%s WHERE sysmlid = ?",
            refId != null && !refId.equalsIgnoreCase(MASTER_BRANCH) ? "_" + refId : "");

        return (Node) getConnection()
            .queryForObject(sql, new Object[]{sysmlId}, new NodeRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public List<Node> findAll() {
        String refId = DbContextHolder.getContext().getBranchId();
        String sql = String.format("SELECT * FROM nodes%s WHERE deleted = false",
            !refId.equalsIgnoreCase(MASTER_BRANCH) ? "_" + refId : "");

        return getConnection().query(sql, new NodeRowMapper());
    }
}
