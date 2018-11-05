package org.openmbee.sdvc.crud.repositories.commit;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.repositories.BaseDAOImpl;

public class CommitDAOImpl extends BaseDAOImpl implements CommitDAO {

    public Commit save(Commit commit) {
        String sql = "INSERT INTO commits (commitType, creator, elasticId, branchId, timestamp) VALUES (?, ?, ?, ?, ?)";

        getConnection().update(sql,
            commit.getCommitType().getId(),
            commit.getCreator(),
            commit.getElasticId(),
            commit.getBranchId(),
            Timestamp.from(commit.getTimestamp())
        );

        return commit;
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
