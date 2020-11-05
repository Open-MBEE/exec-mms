package org.openmbee.mms.core.pubsub;

public interface EmbeddedHookSubscriber {
    Iterable<Class> getSubscriptions();
    void acceptHook(Object payload);
}
