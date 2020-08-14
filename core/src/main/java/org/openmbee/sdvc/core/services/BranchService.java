package org.openmbee.sdvc.core.services;

import org.openmbee.sdvc.core.objects.RefsResponse;
import org.openmbee.sdvc.json.RefJson;

public interface BranchService {

    RefsResponse getBranches(String projectId);

    RefsResponse getBranch(String projectId, String id);

    RefJson createBranch(String projectId, RefJson branch);

    RefsResponse deleteBranch(String projectId, String id);
}
