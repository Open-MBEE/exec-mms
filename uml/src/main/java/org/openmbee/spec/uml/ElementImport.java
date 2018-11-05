package org.openmbee.spec.uml;

public interface ElementImport extends DirectedRelationship, MofObject {

    String getAlias();

    PackageableElement getImportedElement();

    Namespace getImportingNamespace();

    VisibilityKind getVisibility();
}
