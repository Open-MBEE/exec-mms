package org.openmbee.sdvc.crud.repositories.branch;

import java.util.List;
import org.openmbee.sdvc.crud.domains.Branch;

public interface BranchDAO {

    public Branch save(Branch branch);

    public Branch findByBranchId(String sysmlid);

    public List<Branch> findAll();

    public void delete(Branch branch);
}
