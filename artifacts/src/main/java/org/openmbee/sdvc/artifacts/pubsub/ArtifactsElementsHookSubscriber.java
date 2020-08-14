package org.openmbee.sdvc.artifacts.pubsub;

import org.openmbee.sdvc.artifacts.json.ArtifactJson;
import org.openmbee.sdvc.core.pubsub.EmbeddedHookSubscriber;
import org.openmbee.sdvc.crud.hooks.ElementUpdateHook;

import java.util.List;

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
