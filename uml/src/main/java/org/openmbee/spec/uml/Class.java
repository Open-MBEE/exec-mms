package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface Class extends BehavioredClassifier, EncapsulatedClassifier, MofObject {

    Collection<Extension> getExtension();

    Boolean isAbstract();

    Boolean isActive();

    List<Classifier> getNestedClassifier();

    // List<Property> getOwnedAttribute();

    List<Operation> getOwnedOperation();

    Collection<Reception> getOwnedReception();

    // Collection<Class> getSuperClass();
}
