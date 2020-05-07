package org.openmbee.sdvc.core.services;

import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ArtifactService {
    //TODO: decide between byte[] and InputStream
    byte[] get(String projectId, String refId, String id, String commitId, String mimetype, Map<String, String> params);
    ElementsResponse createOrUpdate(String projectId, String refId, String id, MultipartFile file, String user, Map<String, String> params);
}
