package org.openmbee.sdvc.crud.repositories.edge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.data.domains.Edge;
import org.openmbee.sdvc.crud.repositories.BaseDAOImpl;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Component;

@Component
public class EdgeDAOImpl extends BaseDAOImpl implements EdgeDAO {

    private final String INSERT_SQL = "INSERT INTO edges%s (edgeType, child, parent) VALUES (?, ?, ?)";
    private final String UPDATE_SQL = "UPDATE edges%s SET edgeType = ?, child = ?, parent = ? WHERE id = ?";

    public Edge save(Edge edge) {
        String sql = String.format(INSERT_SQL, getSuffix());

        getConnection().update(sql,
            edge.getEdgeType(),
            edge.getChild(),
            edge.getParent()
        );

        return edge;
    }

    public List<Edge> saveAll(List<Edge> edges) {
        List<Edge> newEdges = new ArrayList<>();
        List<Edge> updateEdges = new ArrayList<>();

        for (Edge e: edges) {
            if (e.getId() == null) {
                newEdges.add(e);
            } else {
                updateEdges.add(e);
            }
        }

        if (!newEdges.isEmpty()) {
            insertAll(newEdges);
        }

        if (!updateEdges.isEmpty()) {
            updateAll(updateEdges);
        }
        return edges;
    }

    public List<Edge> insertAll(List<Edge> edges) {
        try {
            //jdbctemplate doesn't have read generated keys for batch, need to use raw jdbc, depends on driver
            Connection rawConn = getConnection().getDataSource().getConnection();
            PreparedStatement ps = rawConn
                .prepareStatement(String.format(INSERT_SQL, getSuffix()), new String[]{"id"});
            for (Edge e: edges) {
                ps.setInt(1, e.getEdgeType());
                ps.setLong(2, e.getChild());
                ps.setLong(3, e.getParent());
                ps.addBatch();
            }
            ps.executeBatch();
            ResultSet rs = ps.getGeneratedKeys();
            int i = 0;
            while (rs.next()) {
                edges.get(i).setId(rs.getLong(1));
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //TODO throw exception to caller
        }
        return edges;
    }

    public List<Edge> updateAll(List<Edge> edges) {
        String updateSql = String.format(UPDATE_SQL, getSuffix());
        getConnection().batchUpdate(updateSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Edge e = edges.get(i);
                ps.setInt(1, e.getEdgeType());
                ps.setLong(2, e.getChild());
                ps.setLong(3, e.getParent());
                ps.setLong(4, e.getId());
            }

            @Override
            public int getBatchSize() {
                return edges.size();
            }
        });
        return edges;
    }

    //TODO these are wrong
    @SuppressWarnings({"unchecked"})
    public Optional<Edge> findParents(String sysmlId, Integer et) {
        String sql = String.format("SELECT * FROM nodes%s WHERE sysmlid = ?",
            getSuffix());

        return (Optional<Edge>) getConnection()
            .queryForObject(sql, new Object[]{sysmlId}, new EdgeRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public Optional<Edge> findChildren(String sysmlId, Integer et) {
        String sql = String.format("SELECT * FROM nodes%s WHERE sysmlid = ?",
            getSuffix());

        return (Optional<Edge>) getConnection()
            .queryForObject(sql, new Object[]{sysmlId}, new EdgeRowMapper());
    }

    public List<Edge> findAll() {
        String sql = String.format("SELECT * FROM edges%s",
            getSuffix());

        return (List<Edge>) getConnection().query(sql, new EdgeRowMapper());
    }
}
