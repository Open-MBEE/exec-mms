package org.openmbee.spec.uml;

public interface ProtocolConformance extends DirectedRelationship, MofObject {

    ProtocolStateMachine getGeneralMachine();

    ProtocolStateMachine getSpecificMachine();
}
