package org.openmbee.sdvc.artifacts.service;

import org.openmbee.sdvc.artifacts.objects.ArtifactResponse;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ArtifactService {
    //TODO: decide between byte[] and InputStream
    ArtifactResponse get(String projectId, String refId, String id, Map<String, String> params);
    ElementsResponse createOrUpdate(String projectId, String refId, String id, MultipartFile file, String user, Map<String, String> params);
    ElementsResponse disassociate(String projectId, String refId, String id, Map<String, String> params);
}
