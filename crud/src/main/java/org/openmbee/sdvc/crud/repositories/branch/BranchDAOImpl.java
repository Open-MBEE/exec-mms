package org.openmbee.sdvc.crud.repositories.branch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.openmbee.sdvc.crud.domains.Branch;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.repositories.BaseDAOImpl;
import org.openmbee.sdvc.crud.repositories.commit.CommitDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class BranchDAOImpl extends BaseDAOImpl implements BranchDAO {

    private CommitDAO commitRepository;

    @Autowired
    public void setCommitRepository(CommitDAO commitRepository) {
        this.commitRepository = commitRepository;
    }

    public Branch save(Branch branch) {
        String sql = "INSERT INTO branches (elasticId, branchId, branchName, parent, parentCommit, timestamp, tag, deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        if (branch.getParentRefId() != null && branch.getParentRef() == null) {
            Branch parentRef = findByBranchId(branch.getParentRefId());
            branch.setParentRef(parentRef);
        } else if (branch.getParentRef() == null) {
            Branch masterRef = findByBranchId("master");
            branch.setParentRef(masterRef);
        }

        if (branch.getParentCommitId() != null && branch.getParentCommit() == null) {
            Commit parentCommit = commitRepository.findByCommitId(branch.getParentCommitId());
            branch.setParentCommit(parentCommit);
        } else if (branch.getParentCommit() == null) {
            Commit latest = commitRepository.findLatest();
            branch.setParentCommit(latest);
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        getConnection().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection)
                throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, branch.getElasticId());
                ps.setString(2, branch.getBranchId());
                ps.setString(3, branch.getBranchName());
                ps.setLong(4, branch.getParentRef().getId());
                ps.setLong(5, branch.getParentCommit().getId());
                ps.setTimestamp(6, Timestamp.from(branch.getTimestamp()));
                ps.setBoolean(7, branch.isTag());
                ps.setBoolean(8, branch.isDeleted());
                return ps;
            }
        }, keyHolder);

        if (keyHolder.getKeyList().isEmpty()) {
            return null;
        }
        branch.setId(keyHolder.getKey().longValue());
        return branch;
    }

    @SuppressWarnings({"unchecked"})
    public Branch findById(long id) {
        String sql = "SELECT * FROM branches WHERE id = ?";

        return (Branch) getConnection()
            .queryForObject(sql, new Object[]{id}, new BranchRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public Branch findByBranchId(String branchId) {
        String sql = "SELECT * FROM branches WHERE branchId = ?";

        return (Branch) getConnection()
            .queryForObject(sql, new Object[]{branchId}, new BranchRowMapper());
    }

    @SuppressWarnings({"unchecked"})
    public List<Branch> findAll() {
        String sql = "SELECT * FROM branches WHERE deleted = false";

        return getConnection().query(sql, new BranchRowMapper());
    }

    public void delete(Branch branch) {
        String sql = "UPDATE branches SET deleted = true WHERE branchId = ?";

        getConnection().update(sql, branch.getBranchId());
    }
}
