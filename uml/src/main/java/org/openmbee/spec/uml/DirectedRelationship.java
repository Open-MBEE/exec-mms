package org.openmbee.spec.uml;

import java.util.Collection;

public interface DirectedRelationship extends Relationship, MofObject {

    Collection<Element> getSource();

    Collection<Element> getTarget();
}
