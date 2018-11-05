package org.openmbee.spec.uml;

import java.util.Collection;

public interface Namespace extends NamedElement, MofObject {

    Collection<ElementImport> getElementImport();

    Collection<PackageableElement> getImportedMember();

    Collection<NamedElement> getMember();

    Collection<NamedElement> getOwnedMember();

    Collection<Constraint> getOwnedRule();

    Collection<PackageImport> getPackageImport();
}
