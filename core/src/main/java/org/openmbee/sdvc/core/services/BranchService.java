package org.openmbee.sdvc.core.services;

import org.openmbee.sdvc.core.objects.BranchesResponse;
import org.openmbee.sdvc.json.RefJson;

public interface BranchService {

    BranchesResponse getBranches(String projectId);

    BranchesResponse getBranch(String projectId, String id);

    RefJson createBranch(String projectId, RefJson branch);
}
