package org.openmbee.spec.uml;

import java.util.Collection;

public interface Port extends Property, MofObject {

    Boolean isBehavior();

    Boolean isConjugated();

    Boolean isService();

    ProtocolStateMachine getProtocol();

    Collection<Interface> getProvided();

    Collection<Port> getRedefinedPort();

    Collection<Interface> getRequired();
}
