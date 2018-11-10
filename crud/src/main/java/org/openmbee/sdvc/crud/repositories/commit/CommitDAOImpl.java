package org.openmbee.sdvc.crud.repositories.commit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.repositories.BaseDAOImpl;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class CommitDAOImpl extends BaseDAOImpl implements CommitDAO {

    public Commit save(Commit commit) {
        String sql = "INSERT INTO commits (commitType, creator, elasticId, branchId, timestamp) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getConnection().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection)
                throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setInt(1, commit.getCommitType().getId());
                ps.setString(2, commit.getCreator());
                ps.setString(3, commit.getElasticId());
                ps.setString(4, commit.getBranchId());
                ps.setTimestamp(5, Timestamp.from(commit.getTimestamp()));

                return ps;
            }
        }, keyHolder);

        if (keyHolder.getKeyList().isEmpty()) {
            return null;
        }
        commit.setId(keyHolder.getKey().longValue());
        return commit;//findById(keyHolder.getKey().longValue());
    }

    @SuppressWarnings({"unchecked"})
    public Commit findById(long id) {
        String sql = "SELECT * FROM commits WHERE id = ?";

        return (Commit) getConnection()
            .queryForObject(sql, new Object[]{id}, new CommitRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public Commit findByCommitId(String commitId) {
        String sql = "SELECT * FROM commits WHERE elasticId = ?";

        return (Commit) getConnection()
            .queryForObject(sql, new Object[]{commitId}, new CommitRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public Commit findByTimestamp(Instant timestamp) {
        String sql = "SELECT * FROM commits WHERE elasticId = ?";

        return (Commit) getConnection()
            .queryForObject(sql, new Object[]{Timestamp.from(timestamp)}, new CommitRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public Commit findLatest() {
        String sql = "SELECT * FROM commits ORDER BY timestamp DESC LIMIT 1";

        return (Commit) getConnection()
            .queryForObject(sql, new CommitRowMapper());
    }


    @SuppressWarnings({"unchecked"})
    public List<Commit> findAll() {
        String sql = "SELECT * FROM commits WHERE deleted = false";

        return getConnection().query(sql, new CommitRowMapper());
    }
}
