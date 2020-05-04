package org.openmbee.sdvc.core.services;

import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ArtifactService {
    //TODO: decide between byte[] and InputStream
    public byte[] get(String projectId, String refId, String id, String commitId, String mimetype, Map<String, String> params);
    public ElementsResponse createOrUpdate(String projectId, String refId, String id, MultipartFile file, Map<String, Object> params);
}
