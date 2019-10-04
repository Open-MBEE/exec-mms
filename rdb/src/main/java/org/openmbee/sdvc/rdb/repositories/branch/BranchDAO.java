package org.openmbee.sdvc.rdb.repositories.branch;

import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.data.domains.scoped.Branch;

public interface BranchDAO {

    public Branch save(Branch branch);

    public Optional<Branch> findById(long id);

    public Optional<Branch> findByBranchId(String sysmlid);

    public List<Branch> findAll();

    public void delete(Branch branch);
}
