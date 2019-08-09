package org.openmbee.sdvc.crud.services;

import org.openmbee.sdvc.rdb.config.DbContextHolder;
import org.openmbee.sdvc.crud.controllers.branches.BranchesResponse;
import org.openmbee.sdvc.rdb.repositories.branch.BranchDAO;
import org.openmbee.sdvc.data.domains.Branch;
import org.openmbee.sdvc.json.RefJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private BranchDAO branchRepository;

    @Autowired
    public void setBranchRepository(BranchDAO branchRepository) {
        this.branchRepository = branchRepository;
    }

    public BranchesResponse getBranches(String projectId) {
        DbContextHolder.setContext(projectId);
        BranchesResponse branchesResponse = new BranchesResponse();
        List<Branch> branches = this.branchRepository.findAll();
        List<RefJson> refs = new ArrayList<>();
        branches.forEach(branch -> {
            refs.add(convertToJson(branch));
        });
        branchesResponse.setBranches(refs);
        return branchesResponse;
    }

    public BranchesResponse getBranch(String projectId, String id) {
        DbContextHolder.setContext(projectId);
        BranchesResponse branchesResponse = new BranchesResponse();
        Optional<Branch> branches = this.branchRepository.findByBranchId(id);
        List<RefJson> refs = new ArrayList<>();
        branches.ifPresent(branch -> {
            refs.add(convertToJson(branch));
        });
        branchesResponse.setBranches(refs);
        return branchesResponse;
    }

    private RefJson convertToJson(Branch branch) {
        RefJson refJson = new RefJson();
        if (branch != null) {
            refJson.setParentRefId(branch.getParentRefId());
            if (branch.getParentCommit() != null) {
                refJson.setParentCommitId(branch.getParentCommit().intValue());
            }
            refJson.setId(branch.getBranchId());
            refJson.setName(branch.getBranchName());
            refJson.setType(branch.isTag() ? "tag" : "branch");
        }
        return refJson;
    }
}
