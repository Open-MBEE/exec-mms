package org.openmbee.spec.uml;

import java.util.List;

public interface InteractionOperand extends InteractionFragment, Namespace, MofObject {

    List<InteractionFragment> getFragment();

    InteractionConstraint getGuard();
}
