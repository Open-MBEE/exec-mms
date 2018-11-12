package org.openmbee.sdvc.crud.repositories.node;

import static org.openmbee.sdvc.crud.config.ContextObject.MASTER_BRANCH;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.repositories.BaseDAOImpl;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class NodeDAOImpl extends BaseDAOImpl implements NodeDAO {

    public Node save(Node node) {
        String sql = String.format(
            "INSERT INTO nodes%s (sysmlid, elasticid, lastcommit, initialcommit, deleted) VALUES (?, ?, ?, ?, ?)",
            getSuffix());
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getConnection().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection)
                throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, node.getSysmlId());
                ps.setString(2, node.getElasticId());
                ps.setString(3, node.getLastCommit());
                ps.setString(4, node.getInitialCommit());
                ps.setBoolean(5, node.isDeleted());

                return ps;
            }
        }, keyHolder);

        if (keyHolder.getKeyList().isEmpty()) {
            return null;
        }
        node.setId(keyHolder.getKey().longValue());
        return node;//findById(keyHolder.getKey().longValue());
    }

    public List<Node> saveAll(List<Node> nodes) {
        return null;
    }

    @SuppressWarnings({"unchecked"})
    public Node findById(long id) {
        String sql = String.format("SELECT * FROM nodes%s WHERE id = ?",
            getSuffix());

        return (Node) getConnection()
            .queryForObject(sql, new Object[]{id}, new NodeRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public Node findBySysmlId(String sysmlId) {
        String sql = String.format("SELECT * FROM nodes%s WHERE sysmlid = ?",
            getSuffix());

        return (Node) getConnection()
            .queryForObject(sql, new Object[]{sysmlId}, new NodeRowMapper());
    }

    public List<Node> findAllBySysmlIds(List<String> ids) {
        return null;
    }

    @SuppressWarnings({"unchecked"})
    public List<Node> findAll() {
        String sql = String.format("SELECT * FROM nodes%s WHERE deleted = false",
            getSuffix());

        return getConnection().query(sql, new NodeRowMapper());
    }

}
