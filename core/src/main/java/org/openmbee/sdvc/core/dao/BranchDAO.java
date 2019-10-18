package org.openmbee.sdvc.core.dao;

import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.data.domains.scoped.Branch;

public interface BranchDAO {

    public Branch save(Branch branch);

    public Optional<Branch> findByBranchId(String sysmlid);

    public List<Branch> findAll();

    public void delete(Branch branch);
}
