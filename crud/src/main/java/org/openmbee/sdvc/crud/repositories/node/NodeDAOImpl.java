package org.openmbee.sdvc.crud.repositories.node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.repositories.BaseDAOImpl;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class NodeDAOImpl extends BaseDAOImpl implements NodeDAO {

    private final String INSERT_SQL = "INSERT INTO nodes%s (nodeid, indexid, lastcommit, initialcommit, deleted, nodetype) VALUES (?, ?, ?, ?, ?, ?)";
    private final String UPDATE_SQL = "UPDATE nodes%s SET nodeid = ?, indexid = ?, lastcommit = ?, initialcommit = ?, deleted = ?, nodetype = ? WHERE id = ?";

    public Node save(Node node) {
        if (node.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            getConnection().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection)
                    throws SQLException {
                    PreparedStatement ps = connection
                        .prepareStatement(String.format(INSERT_SQL, getSuffix()),
                            new String[]{"id"});
                    ps.setString(1, node.getNodeId());
                    ps.setString(2, node.getIndexId());
                    ps.setString(3, node.getLastCommit());
                    ps.setString(4, node.getInitialCommit());
                    ps.setBoolean(5, node.isDeleted());
                    ps.setInt(6, node.getNodeType());
                    return ps;
                }
            }, keyHolder);

            if (keyHolder.getKeyList().isEmpty()) {
                return null; //TODO error?
            }
            node.setId(keyHolder.getKey().longValue());
        } else {
            getConnection().update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection)
                    throws SQLException {
                    PreparedStatement ps = connection
                        .prepareStatement(String.format(UPDATE_SQL, getSuffix()));
                    ps.setString(1, node.getNodeId());
                    ps.setString(2, node.getIndexId());
                    ps.setString(3, node.getLastCommit());
                    ps.setString(4, node.getInitialCommit());
                    ps.setBoolean(5, node.isDeleted());
                    ps.setInt(6, node.getNodeType());
                    ps.setLong(7, node.getId());
                    return ps;
                }
            });
        }
        return node;
    }

    //TODO handle errors
    public List<Node> saveAll(List<Node> nodes) {
        List<Node> newNodes = new ArrayList<>();
        List<Node> updateNodes = new ArrayList<>();

        for (Node n : nodes) {
            if (n.getId() == null) {
                newNodes.add(n);
            } else {
                updateNodes.add(n);
            }
        }

        if (!newNodes.isEmpty()) {
            insertAll(newNodes);
        }

        if (!updateNodes.isEmpty()) {
            updateAll(updateNodes);
        }
        return nodes;
    }

    public List<Node> insertAll(List<Node> nodes) {
        try {
            //jdbctemplate doesn't have read generated keys for batch, need to use raw jdbc, depends on driver
            Connection rawConn = getConnection().getDataSource().getConnection();
            PreparedStatement ps = rawConn
                .prepareStatement(String.format(INSERT_SQL, getSuffix()), new String[]{"id"});
            for (Node n : nodes) {
                ps.setString(1, n.getNodeId());
                ps.setString(2, n.getIndexId());
                ps.setString(3, n.getLastCommit());
                ps.setString(4, n.getInitialCommit());
                ps.setBoolean(5, n.isDeleted());
                ps.setInt(6, n.getNodeType());
                ps.addBatch();
            }
            ps.executeBatch();
            ResultSet rs = ps.getGeneratedKeys();
            int i = 0;
            while (rs.next()) {
                nodes.get(i).setId(rs.getLong(1));
                i++;
            }
        } catch (SQLException e) {
            //TODO throw exception to caller
        }
        return nodes;
    }

    public List<Node> updateAll(List<Node> nodes) {
        String updateSql = String.format(UPDATE_SQL, getSuffix());
        getConnection().batchUpdate(updateSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Node n = nodes.get(i);
                ps.setString(1, n.getNodeId());
                ps.setString(2, n.getIndexId());
                ps.setString(3, n.getLastCommit());
                ps.setString(4, n.getInitialCommit());
                ps.setBoolean(5, n.isDeleted());
                ps.setInt(6, n.getNodeType());
                ps.setLong(7, n.getId());
            }

            @Override
            public int getBatchSize() {
                return nodes.size();
            }
        });
        return nodes;
    }

    @SuppressWarnings({"unchecked"})
    public Node findById(long id) {
        String sql = String.format("SELECT * FROM nodes%s WHERE id = ?",
            getSuffix());

        return (Node) getConnection()
            .queryForObject(sql, new Object[]{id}, new NodeRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public Node findByNodeId(String nodeId) {
        String sql = String.format("SELECT * FROM nodes%s WHERE nodeid = ?",
            getSuffix());

        return (Node) getConnection()
            .queryForObject(sql, new Object[]{nodeId}, new NodeRowMapper());
    }

    public List<Node> findAllByNodeIds(Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        String sql = String.format("SELECT * FROM nodes%s WHERE nodeid IN (%s)",
            getSuffix(), "'" + String.join("','", ids) + "'");
        return getConnection().query(sql, new NodeRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public List<Node> findAll() {
        String sql = String.format("SELECT * FROM nodes%s WHERE deleted = FALSE",
            getSuffix());

        return getConnection().query(sql, new NodeRowMapper());
    }

}
