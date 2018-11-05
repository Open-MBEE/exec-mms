package org.openmbee.spec.uml;

import java.util.Collection;

public interface Vertex extends NamedElement, RedefinableElement, MofObject {

    Region getContainer();

    Collection<Transition> getIncoming();

    Collection<Transition> getOutgoing();

    // Collection<Classifier> getRedefinitionContext();

    Vertex getRedefinedVertex();
}
