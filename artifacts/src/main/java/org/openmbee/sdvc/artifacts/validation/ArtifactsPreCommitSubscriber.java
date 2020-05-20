package org.openmbee.sdvc.artifacts.validation;

import org.openmbee.sdvc.artifacts.controllers.ArtifactController;
import org.openmbee.sdvc.artifacts.json.ArtifactJson;
import org.openmbee.sdvc.core.utils.RequestUtils;
import org.openmbee.sdvc.core.validation.PreCommitSubscriber;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;

import java.util.List;
import java.util.Map;

public class ArtifactsPreCommitSubscriber implements PreCommitSubscriber {

    private RequestUtils requestUtils;

    @Autowired
    public void setRequestUtils(RequestUtils requestUtils) {
        this.requestUtils = requestUtils;
    }

    @Override
    public void preCommitElementChanges(String projectId, String refId, List<ElementJson> elements, Map<String, String> params, String user) {
        HandlerMethod handlerMethod = requestUtils.getHandlerMethod();
        boolean allowArtifactChanges = handlerMethod.getBeanType().equals(ArtifactController.class);

        if(!allowArtifactChanges) {
            //Ignore any artifact changes coming in outside of the Artifacts Controller
            elements.parallelStream().forEach(v -> {
                v.remove(ArtifactJson.ARTIFACTS);
            });
        }
    }
}
