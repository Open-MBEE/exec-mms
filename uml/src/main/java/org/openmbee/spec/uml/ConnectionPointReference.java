package org.openmbee.spec.uml;

import java.util.Collection;

public interface ConnectionPointReference extends Vertex, MofObject {

    Collection<Pseudostate> getEntry();

    Collection<Pseudostate> getExit();

    State getState();
}
