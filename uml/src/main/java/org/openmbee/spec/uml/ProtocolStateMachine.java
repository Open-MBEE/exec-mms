package org.openmbee.spec.uml;

import java.util.Collection;

public interface ProtocolStateMachine extends StateMachine, MofObject {

    Collection<ProtocolConformance> getConformance();
}
