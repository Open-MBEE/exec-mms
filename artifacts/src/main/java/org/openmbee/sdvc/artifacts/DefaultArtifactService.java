package org.openmbee.sdvc.artifacts;

import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.core.services.ArtifactService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class DefaultArtifactService implements ArtifactService {
    @Override
    public byte[] get(String projectId, String refId, String id, String commitId, String mimetype, Map<String, String> params) {
        //TODO: Implement this
        return new byte[0];
    }

    @Override
    public ElementsResponse createOrUpdate(String projectId, String refId, String id, MultipartFile file, Map<String, Object> params) {
        //TODO: Implement this
        return null;
    }
}
