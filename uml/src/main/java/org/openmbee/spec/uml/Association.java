package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface Association extends Relationship, Classifier, MofObject {

    Collection<Type> getEndType();

    Boolean isDerived();

    List<Property> getMemberEnd();

    Collection<Property> getNavigableOwnedEnd();

    List<Property> getOwnedEnd();
}
