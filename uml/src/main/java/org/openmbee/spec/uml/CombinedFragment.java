package org.openmbee.spec.uml;

import java.util.Collection;
import java.util.List;

public interface CombinedFragment extends InteractionFragment, MofObject {

    Collection<Gate> getCfragmentGate();

    InteractionOperatorKind getInteractionOperator();

    List<InteractionOperand> getOperand();
}
