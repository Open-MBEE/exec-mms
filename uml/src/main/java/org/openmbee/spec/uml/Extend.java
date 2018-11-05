package org.openmbee.spec.uml;

import java.util.List;

public interface Extend extends NamedElement, DirectedRelationship, MofObject {

    Constraint getCondition();

    UseCase getExtendedCase();

    UseCase getExtension();

    List<ExtensionPoint> getExtensionLocation();
}
