package org.openmbee.sdvc.core.services;

import org.openmbee.sdvc.core.objects.CommitsRequest;
import org.openmbee.sdvc.core.objects.CommitsResponse;

import java.util.Map;

public interface CommitService {
    CommitsResponse getRefCommits(String projectId, String refId, Map<String, String> params);
    CommitsResponse getCommit(String projectId, String commitId);
    CommitsResponse getElementCommits(String projectId, String refId, String elementId, Map<String, String> params);
    CommitsResponse getCommits(String projectId, CommitsRequest req);
    boolean isProjectNew(String projectId);
}
