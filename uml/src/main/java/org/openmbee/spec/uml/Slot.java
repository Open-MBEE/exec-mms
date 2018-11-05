package org.openmbee.spec.uml;

import java.util.List;

public interface Slot extends Element, MofObject {

    StructuralFeature getDefiningFeature();

    InstanceSpecification getOwningInstance();

    List<ValueSpecification> getValue();
}
