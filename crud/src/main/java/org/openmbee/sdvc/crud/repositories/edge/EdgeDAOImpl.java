package org.openmbee.sdvc.crud.repositories.edge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

        getConn().update(sql,
            edge.getEdgeType(),
            edge.getChild(),
            edge.getParent()
        );

        return edge;
    }

    public List<Edge> saveAll(List<Edge> edges) throws SQLException {
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

    public List<Edge> insertAll(List<Edge> edges) throws SQLException {
        //jdbctemplate doesn't have read generated keys for batch, need to use raw jdbc, depends on driver
        Connection rawConn = getConn().getDataSource().getConnection();
        PreparedStatement ps = rawConn
            .prepareStatement(String.format(INSERT_SQL, getSuffix()), new String[]{"id"});
        for (Edge e : edges) {
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

        return edges;
    }

    public List<Edge> updateAll(List<Edge> edges) {
        String updateSql = String.format(UPDATE_SQL, getSuffix());
        getConn().batchUpdate(updateSql, new BatchPreparedStatementSetter() {
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

    public void deleteAll(List<Edge> edges) {
        String deleteSql = String.format("DELETE FROM edges%s WHERE id = ?", getSuffix());
        getConn().batchUpdate(deleteSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Edge e = edges.get(i);
                ps.setLong(1, e.getId());
            }

            @Override
            public int getBatchSize() {
                return edges.size();
            }
        });
    }

    @SuppressWarnings({"unchecked"})
    public List<Edge> findParents(String sysmlId, Integer et) {
        String sql = String.format("SELECT * FROM edges%1$s WHERE child = (SELECT id FROM nodes%1$s WHERE sysmlid = ?)", getSuffix());
        return (List<Edge>) getConn().query(sql, new Object[]{sysmlId}, new EdgeRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public List<Edge> findChildren(String sysmlId, Integer et) {
        String sql = String.format("SELECT * FROM edges%1$s WHERE parent = (SELECT id FROM nodes%1$s WHERE sysmlid = ?)", getSuffix());
        return (List<Edge>) getConn().query(sql, new Object[]{sysmlId}, new EdgeRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public List<Edge> findAll() {
        String sql = String.format("SELECT * FROM edges%s", getSuffix());
        return (List<Edge>) getConn().query(sql, new EdgeRowMapper());
    }
}
