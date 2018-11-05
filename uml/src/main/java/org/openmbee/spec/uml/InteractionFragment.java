package org.openmbee.spec.uml;

import java.util.Collection;

public interface InteractionFragment extends NamedElement, MofObject {

    Collection<Lifeline> getCovered();

    Interaction getEnclosingInteraction();

    InteractionOperand getEnclosingOperand();

    Collection<GeneralOrdering> getGeneralOrdering();
}
