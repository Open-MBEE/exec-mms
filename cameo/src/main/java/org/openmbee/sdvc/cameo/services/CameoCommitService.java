package org.openmbee.sdvc.cameo.services;

import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.services.CommitService;
import org.openmbee.sdvc.crud.services.DefaultCommitService;
import org.openmbee.sdvc.data.domains.scoped.Commit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("cameoCommitService")
public class CameoCommitService extends DefaultCommitService implements CommitService {
    @Override
    public boolean isProjectNew(String projectId) {
        ContextHolder.setContext(projectId);
        List<Commit> commits = commitRepository.findAll();
        return commits == null || commits.size() <= 1; // a cameo project gets 1 auto commit, so its still "new"
    }
}
