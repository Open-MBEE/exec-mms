package org.openmbee.spec.uml;

import java.util.Collection;

public interface ProtocolTransition extends Transition, MofObject {

    Constraint getPostCondition();

    Constraint getPreCondition();

    Collection<Operation> getReferred();
}
