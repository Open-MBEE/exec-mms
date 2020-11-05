package org.openmbee.mms.artifacts.service;

import org.openmbee.mms.artifacts.objects.ArtifactResponse;
import org.openmbee.mms.core.objects.ElementsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ArtifactService {
    ArtifactResponse get(String projectId, String refId, String id, Map<String, String> params);
    ElementsResponse createOrUpdate(String projectId, String refId, String id, MultipartFile file, String user, Map<String, String> params);
    ElementsResponse disassociate(String projectId, String refId, String id, String user, Map<String, String> params);
}
