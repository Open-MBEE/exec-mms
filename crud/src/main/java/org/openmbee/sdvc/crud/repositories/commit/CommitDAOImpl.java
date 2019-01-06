package org.openmbee.sdvc.crud.repositories.commit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.crud.domains.Branch;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.repositories.BaseDAOImpl;
import org.openmbee.sdvc.crud.repositories.branch.BranchDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class CommitDAOImpl extends BaseDAOImpl implements CommitDAO {

    private BranchDAO branchRepository;

    @Autowired
    public void setBranchRepository(BranchDAO branchRepository) {
        this.branchRepository = branchRepository;
    }

    public Commit save(Commit commit) {
        String sql = "INSERT INTO commits (commitType, creator, indexId, branchId, timestamp, comment) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getConnection().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection)
                throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setInt(1, commit.getCommitType().getId());
                ps.setString(2, commit.getCreator());
                ps.setString(3, commit.getIndexId());
                ps.setString(4, commit.getBranchId());
                ps.setTimestamp(5, Timestamp.from(commit.getTimestamp()));
                ps.setString(6, commit.getComment());

                return ps;
            }
        }, keyHolder);

        if (keyHolder.getKeyList().isEmpty()) {
            return null;//TODO error
        }
        commit.setId(keyHolder.getKey().longValue());
        return commit;
    }

    public Optional<Commit> findById(long id) {
        String sql = "SELECT * FROM commits WHERE id = ?";

        List<Commit> l = getConnection()
            .query(sql, new Object[]{id}, new CommitRowMapper());
        return l.isEmpty() ? Optional.empty() : Optional.of(l.get(0));

    }

    public Optional<Commit> findByCommitId(String commitId) {
        String sql = "SELECT * FROM commits WHERE indexid = ?";

        List<Commit> l = getConnection()
            .query(sql, new Object[]{commitId}, new CommitRowMapper());
        return l.isEmpty() ? Optional.empty() : Optional.of(l.get(0));

    }

    public Optional<Commit> findByRefAndTimestamp(String refId, Instant timestamp) {
        List<Commit> res = findByRefAndTimestampAndLimit(refId, timestamp, 1);
        if (!res.isEmpty()) {
            return Optional.of(res.get(0));
        }
        return Optional.empty();
    }

    public Optional<Commit> findLatestByRef(String refId) {
        List<Commit> res = findByRefAndTimestampAndLimit(refId, null, 1);
        if (!res.isEmpty()) {
            return Optional.of(res.get(0));
        }
        return Optional.empty();
    }

    public List<Commit> findAll() {
        String sql = "SELECT * FROM commits ORDER BY timestamp DESC";
        return getConnection().query(sql, new CommitRowMapper());
    }

    //TODO should this be here or in service instead, this introduces dependency to branch dao
    public List<Commit> findByRefAndTimestampAndLimit(String refId, Instant timestamp, int limit) {
        List<Commit> commits = new ArrayList<>();
        String currentRef = refId;
        Long currentCid = 0L;
        while (currentRef != null && (commits.size() < limit || limit == 0)) {
            int currentLimit = limit == 0 ? 0 : limit - commits.size();
            List<Commit> next = findByRefAndLimit(currentRef, currentCid, timestamp, currentLimit);
            commits.addAll(next);
            Optional<Branch> ref = branchRepository.findByBranchId(currentRef);
            if (ref.isPresent()) {
                currentRef = ref.get().getParentRefId();
                currentCid = ref.get().getParentCommit();
            }

            if (currentRef != null && currentRef.equals("")) {
                currentRef = null;
            }
        }
        return commits;
    }

    private List<Commit> findByRefAndLimit(String refId, Long cid, Instant timestamp, int limit) {
        int commitCol = 0;
        int timestampCol = 0;
        int limitCol = 0;
        int currentExtraCol = 2;
        StringBuilder query = new StringBuilder("SELECT * FROM commits WHERE branchid = ?");
        if (cid != null && cid != 0) {
            query.append(" AND timestamp <= (SELECT timestamp FROM commits WHERE id = ?)");
            commitCol = currentExtraCol;
            currentExtraCol++;
        }
        if (timestamp != null) {
            query.append(" AND date_trunc('milliseconds', timestamp) <= ?");
            timestampCol = currentExtraCol;
            currentExtraCol++;
        }
        query.append(" ORDER BY timestamp DESC");
        if (limit != 0) {
            query.append(" LIMIT ?");
            limitCol = currentExtraCol;
            currentExtraCol++;
        }
        final int commitColNum = commitCol;
        final int timestampColNum = timestampCol;
        final int limitColNum = limitCol;
        return getConnection().query(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection)
                throws SQLException {
                PreparedStatement ps = connection
                    .prepareStatement(query.toString());
                ps.setString(1, refId);
                if (commitColNum != 0) {
                    ps.setLong(commitColNum, cid);
                }
                if (timestampColNum != 0) {
                    ps.setTimestamp(timestampColNum, Timestamp.from(timestamp));
                }
                if (limitColNum != 0) {
                    ps.setInt(limitColNum, limit);
                }
                return ps;
            }
        }, new CommitRowMapper());
    }

}
