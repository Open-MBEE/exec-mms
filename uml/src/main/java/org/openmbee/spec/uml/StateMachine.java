package org.openmbee.spec.uml;

import java.util.Collection;

public interface StateMachine extends Behavior, MofObject {

    Collection<Pseudostate> getConnectionPoint();

    // Collection<StateMachine> getExtendedStateMachine();

    Collection<Region> getRegion();

    Collection<State> getSubmachineState();
}
