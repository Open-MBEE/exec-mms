package org.openmbee.sdvc.crud.repositories.branch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.crud.domains.Branch;
import org.openmbee.sdvc.crud.repositories.BaseDAOImpl;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class BranchDAOImpl extends BaseDAOImpl implements BranchDAO {
/*
    private CommitDAO commitRepository;

    @Autowired
    public void setCommitRepository(CommitDAO commitRepository) {
        this.commitRepository = commitRepository;
    }
*/
    public Branch save(Branch branch) {
        String sql = "INSERT INTO branches (description, branchId, branchName, parentRefId, parentCommit, timestamp, tag, deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
/*
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
*/
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getConnection().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection)
                throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, branch.getDescription());
                ps.setString(2, branch.getBranchId());
                ps.setString(3, branch.getBranchName());
                ps.setString(4, branch.getParentRefId());
                ps.setLong(5, branch.getParentCommit());
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

    public Optional<Branch> findById(long id) {
        String sql = "SELECT * FROM branches WHERE id = ?";

        List<Branch> l = getConnection()
            .query(sql, new Object[]{id}, new BranchRowMapper());
        return l.isEmpty() ? Optional.empty() : Optional.of(l.get(0));
    }

    public Optional<Branch> findByBranchId(String branchId) {
        String sql = "SELECT * FROM branches WHERE branchId = ?";

        List<Branch> l = getConnection()
            .query(sql, new Object[]{branchId}, new BranchRowMapper());
        return l.isEmpty() ? Optional.empty() : Optional.of(l.get(0));
    }

    public List<Branch> findAll() {
        String sql = "SELECT * FROM branches WHERE deleted = false";

        return getConnection().query(sql, new BranchRowMapper());
    }

    public void delete(Branch branch) {
        String sql = "UPDATE branches SET deleted = true WHERE branchId = ?";

        getConnection().update(sql, branch.getBranchId());
    }
}
