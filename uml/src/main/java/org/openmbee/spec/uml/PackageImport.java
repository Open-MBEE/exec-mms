package org.openmbee.spec.uml;

public interface PackageImport extends DirectedRelationship, MofObject {

    Package getImportedPackage();

    Namespace getImportingNamespace();

    VisibilityKind getVisibility();
}
