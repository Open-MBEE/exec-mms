package org.openmbee.spec.uml;

import java.util.Collection;

public interface ObjectNode extends TypedElement, ActivityNode, MofObject {

    Collection<State> getInState();

    Boolean isControlType();

    ObjectNodeOrderingKind getOrdering();

    Behavior getSelection();

    ValueSpecification getUpperBound();
}
