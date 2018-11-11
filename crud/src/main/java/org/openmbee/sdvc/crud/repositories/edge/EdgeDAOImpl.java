package org.openmbee.sdvc.crud.repositories.edge;

import static org.openmbee.sdvc.crud.config.ContextObject.MASTER_BRANCH;

import java.util.List;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.domains.Edge;
import org.openmbee.sdvc.crud.domains.EdgeType;
import org.openmbee.sdvc.crud.repositories.BaseDAOImpl;
import org.springframework.stereotype.Component;

@Component
public class EdgeDAOImpl extends BaseDAOImpl implements EdgeDAO {

    public Edge save(Edge edge) {
        String sql = String.format(
            "INSERT INTO edges%s (edgeType, child_id, parent_id) VALUES (?, ?, ?)",
            getSuffix());

        getConnection().update(sql,
            edge.getEdgeType(),
            edge.getChild().getId(),
            edge.getParent().getId()
        );

        return edge;
    }

    @SuppressWarnings({"unchecked"})
    public Edge findParents(String sysmlId, EdgeType et) {
        String sql = String.format("SELECT * FROM nodes%s WHERE sysmlid = ?",
            getSuffix());

        return (Edge) getConnection()
            .queryForObject(sql, new Object[]{sysmlId}, new EdgeRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public Edge findChildren(String sysmlId, EdgeType et) {
        String sql = String.format("SELECT * FROM nodes%s WHERE sysmlid = ?",
            getSuffix());

        return (Edge) getConnection()
            .queryForObject(sql, new Object[]{sysmlId}, new EdgeRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public List<Edge> findAll() {
        String sql = String.format("SELECT * FROM nodes%s WHERE deleted = false",
            getSuffix());

        return getConnection().query(sql, new EdgeRowMapper());
    }
}
