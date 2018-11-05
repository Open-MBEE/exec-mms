package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface InteractionUse extends InteractionFragment, MofObject {

    Collection<Gate> getActualGate();

    List<ValueSpecification> getArgument();

    Interaction getRefersTo();

    ValueSpecification getReturnValue();

    Property getReturnValueRecipient();
}
