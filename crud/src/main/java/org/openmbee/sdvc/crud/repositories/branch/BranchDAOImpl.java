package org.openmbee.sdvc.crud.repositories.branch;

import java.sql.Timestamp;
import java.util.List;
import org.openmbee.sdvc.crud.domains.Branch;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.repositories.BaseDAOImpl;
import org.openmbee.sdvc.crud.repositories.commit.CommitDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class BranchDAOImpl extends BaseDAOImpl implements BranchDAO {

    private CommitDAOImpl commitRepository;

    @Autowired
    public void setCommitRepository(CommitDAOImpl commitRepository) {
        this.commitRepository = commitRepository;
    }

    public Branch save(Branch branch) {
        String sql = "INSERT INTO branches (elasticId, branchId, branchName, parent, parentCommit, tag, deleted, created, creator, modified, modifier) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

        getConnection().update(sql,
            branch.getElasticId(),
            branch.getBranchId(),
            branch.getBranchName(),
            branch.getParentRef() != null ? branch.getParentRef().getId() : null,
            branch.getParentCommit() != null ? branch.getParentCommit().getId() : null,
            branch.isTag(),
            branch.isDeleted(),
            Timestamp.from(branch.getCreated()),
            branch.getCreator(),
            Timestamp.from(branch.getModified()),
            branch.getModifier()
        );

        return branch;
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
