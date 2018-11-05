package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface Interface extends Classifier, MofObject {

    List<Classifier> getNestedClassifier();

    List<Property> getOwnedAttribute();

    List<Operation> getOwnedOperation();

    Collection<Reception> getOwnedReception();

    ProtocolStateMachine getProtocol();

    Collection<Interface> getRedefinedInterface();
}
