package org.openmbee.sdvc.crud.repositories.edge;

import static org.openmbee.sdvc.crud.config.ContextObject.MASTER_BRANCH;

import java.sql.Timestamp;
import java.util.List;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.domains.Edge;
import org.openmbee.sdvc.crud.domains.EdgeType;
import org.openmbee.sdvc.crud.repositories.BaseDAOImpl;

public class EdgeDAOImpl extends BaseDAOImpl implements EdgeDAO {

    public Edge save(Edge edge) {
        String refId = DbContextHolder.getContext().getBranchId();
        String sql = String.format(
            "INSERT INTO edges%s (edgeType, child_id, parent_id, created, modified) VALUES (?, ?, ?, ?, ?)",
            !refId.equalsIgnoreCase(MASTER_BRANCH) ? "_" + refId : "");

        getConnection().update(sql,
            edge.getEdgeType(),
            edge.getChild().getId(),
            edge.getParent().getId(),
            Timestamp.from(edge.getCreated()),
            Timestamp.from(edge.getModified())
        );

        return edge;
    }

    @SuppressWarnings({"unchecked"})
    public Edge findParents(String sysmlId, EdgeType et) {
        String refId = DbContextHolder.getContext().getBranchId();
        String sql = String.format("SELECT * FROM nodes%s WHERE sysmlid = ?",
            refId != null && !refId.equalsIgnoreCase(MASTER_BRANCH) ? "_" + refId : "");

        return (Edge) getConnection()
            .queryForObject(sql, new Object[]{sysmlId}, new EdgeRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public Edge findChildren(String sysmlId, EdgeType et) {
        String refId = DbContextHolder.getContext().getBranchId();
        String sql = String.format("SELECT * FROM nodes%s WHERE sysmlid = ?",
            refId != null && !refId.equalsIgnoreCase(MASTER_BRANCH) ? "_" + refId : "");

        return (Edge) getConnection()
            .queryForObject(sql, new Object[]{sysmlId}, new EdgeRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public List<Edge> findAll() {
        String refId = DbContextHolder.getContext().getBranchId();
        String sql = String.format("SELECT * FROM nodes%s WHERE deleted = false",
            !refId.equalsIgnoreCase(MASTER_BRANCH) ? "_" + refId : "");

        return getConnection().query(sql, new EdgeRowMapper());
    }
}
