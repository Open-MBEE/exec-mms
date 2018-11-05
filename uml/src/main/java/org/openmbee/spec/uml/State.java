package org.openmbee.spec.uml;

import java.util.Collection;

public interface State extends Namespace, Vertex, MofObject {

    Collection<ConnectionPointReference> getConnection();

    Collection<Pseudostate> getConnectionPoint();

    Collection<Trigger> getDeferrableTrigger();

    Behavior getDoActivity();

    Behavior getEntry();

    Behavior getExit();

    Boolean isComposite();

    Boolean isOrthogonal();

    Boolean isSimple();

    Boolean isSubmachineState();

    Collection<Region> getRegion();

    Constraint getStateInvariant();

    StateMachine getSubmachine();
}
