package org.openmbee.sdvc.core.dao;

import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.data.domains.scoped.Branch;

public interface BranchDAO {

    Branch save(Branch branch);

    Optional<Branch> findByBranchId(String sysmlid);

    List<Branch> findAll();

    void delete(Branch branch);
}
