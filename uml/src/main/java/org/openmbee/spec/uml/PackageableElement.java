package org.openmbee.spec.uml;

public interface PackageableElement extends ParameterableElement, NamedElement, MofObject {

    VisibilityKind getVisibility();
}
