package org.openmbee.sdvc.core.pubsub;

public interface EmbeddedHookSubscriber {
    Iterable<Class> getSubscriptions();
    void acceptHook(Object payload);
}
