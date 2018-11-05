package org.openmbee.spec.uml;

import java.util.Collection;

public interface Lifeline extends NamedElement, MofObject {

    Collection<InteractionFragment> getCoveredBy();

    PartDecomposition getDecomposedAs();

    Interaction getInteraction();

    ConnectableElement getRepresents();

    ValueSpecification getSelector();
}
