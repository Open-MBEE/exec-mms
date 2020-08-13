package org.openmbee.sdvc.rdb.repositories;

import java.util.Optional;
import org.openmbee.sdvc.core.dao.BranchGDAO;
import org.openmbee.sdvc.data.domains.global.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BranchGDAOImpl implements BranchGDAO {

    private BranchRepository branchRepository;

    @Autowired
    public void setBranchRepository(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public Optional<Branch> findByProject_ProjectIdAndBranchId(
        String projectId, String branchId) {
        return Optional.empty();
    }

    @Override
    public Branch save(Branch branch) {
        return null;
    }
}
