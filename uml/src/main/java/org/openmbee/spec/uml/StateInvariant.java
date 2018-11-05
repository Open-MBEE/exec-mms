package org.openmbee.spec.uml;

public interface StateInvariant extends InteractionFragment, MofObject {
    // Collection<Lifeline> getCovered();

    Constraint getInvariant();
}
