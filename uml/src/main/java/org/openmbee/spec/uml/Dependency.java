package org.openmbee.spec.uml;

import java.util.Collection;

public interface Dependency extends DirectedRelationship, PackageableElement, MofObject {

    Collection<NamedElement> getClient();

    Collection<NamedElement> getSupplier();
}
