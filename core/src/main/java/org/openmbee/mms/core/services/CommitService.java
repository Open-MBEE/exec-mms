package org.openmbee.mms.core.services;

import org.openmbee.mms.core.objects.CommitsRequest;
import org.openmbee.mms.core.objects.CommitsResponse;

import java.util.Map;

public interface CommitService {
    CommitsResponse getRefCommits(String projectId, String refId, Map<String, String> params);
    CommitsResponse getCommit(String projectId, String commitId);
    CommitsResponse getElementCommits(String projectId, String refId, String elementId, Map<String, String> params);
    CommitsResponse getCommits(String projectId, CommitsRequest req);
    boolean isProjectNew(String projectId);
}
