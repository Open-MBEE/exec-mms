package org.openmbee.mms.core.pubsub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmbeddedHookService {

    private Map<Class, Collection<EmbeddedHookSubscriber>> embeddedHookSubscribers = new HashMap<>();

    @Autowired (required = false)
    public void setEmbeddedHookSubscribers(List<EmbeddedHookSubscriber> embeddedHookSubscribers) {
        if(embeddedHookSubscribers != null) {
            for(EmbeddedHookSubscriber subscriber : embeddedHookSubscribers) {
                for(Class clz : subscriber.getSubscriptions()) {
                    Collection<EmbeddedHookSubscriber> subscribersForHook = this.embeddedHookSubscribers.get(clz);
                    if(subscribersForHook == null) {
                        subscribersForHook = new ArrayList<>();
                        this.embeddedHookSubscribers.put(clz, subscribersForHook);
                    }
                    subscribersForHook.add(subscriber);
                }
            }
        }
    }

    public void hook(Object payload) {
        Collection<EmbeddedHookSubscriber> subscribersForHook = this.embeddedHookSubscribers.get(payload.getClass());
        if(subscribersForHook != null) {
            subscribersForHook.forEach(v -> v.acceptHook(payload));
        }
    }

}
