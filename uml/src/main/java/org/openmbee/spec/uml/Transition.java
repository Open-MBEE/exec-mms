package org.openmbee.spec.uml;

import java.util.Collection;

public interface Transition extends Namespace, RedefinableElement, MofObject {

    Region getContainer();

    Behavior getEffect();

    Constraint getGuard();

    TransitionKind getKind();

    Transition getRedefinedTransition();

    // Collection<Classifier> getRedefinitionContext();

    Vertex getSource();

    Vertex getTarget();

    Collection<Trigger> getTrigger();
}
