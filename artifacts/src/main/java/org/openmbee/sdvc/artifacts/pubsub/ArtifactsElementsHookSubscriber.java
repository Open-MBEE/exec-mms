package org.openmbee.sdvc.artifacts.pubsub;

import org.openmbee.sdvc.artifacts.controllers.ArtifactController;
import org.openmbee.sdvc.artifacts.json.ArtifactJson;
import org.openmbee.sdvc.core.utils.RequestUtils;
import org.openmbee.sdvc.core.pubsub.EmbeddedHookSubscriber;
import org.openmbee.sdvc.crud.hooks.ElementUpdateHook;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;

import java.util.List;
import java.util.Map;

public class ArtifactsElementsHookSubscriber implements EmbeddedHookSubscriber {

    @Override
    public Iterable<Class> getSubscriptions() {
        return List.of(ElementUpdateHook.class);
    }

    @Override
    public void acceptHook(Object payload) {
        if(! (payload instanceof ElementUpdateHook)) {
            return;
        }

        ElementUpdateHook elementUpdateHook = (ElementUpdateHook) payload;
        if(elementUpdateHook.getElements() != null) {
            //Ignore any artifact changes coming in outside of the Artifacts Controller
            elementUpdateHook.getElements().parallelStream().forEach(v -> {
                v.remove(ArtifactJson.ARTIFACTS);
            });
        }
    }
}
