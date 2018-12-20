package org.openmbee.sdvc.crud.repositories.branch;

import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.crud.domains.Branch;

public interface BranchDAO {

    public Optional<Branch> save(Branch branch);

    public Optional<Branch> findById(long id);

    public Optional<Branch> findByBranchId(String sysmlid);

    public List<Branch> findAll();

    public void delete(Branch branch);
}
