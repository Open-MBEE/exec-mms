package org.openmbee.spec.uml;

public interface Pseudostate extends Vertex, MofObject {

    PseudostateKind getKind();

    State getState();

    StateMachine getStateMachine();
}
