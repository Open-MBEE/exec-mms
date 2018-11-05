package org.openmbee.spec.uml;

import java.util.Collection;

public interface Region extends Namespace, RedefinableElement, MofObject {

    Region getExtendedRegion();

    // Collection<Classifier> getRedefinitionContext();

    State getState();

    StateMachine getStateMachine();

    Collection<Vertex> getSubvertex();

    Collection<Transition> getTransition();
}
