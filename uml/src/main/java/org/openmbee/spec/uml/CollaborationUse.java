package org.openmbee.spec.uml;

import java.util.Collection;

public interface CollaborationUse extends NamedElement, MofObject {

    Collection<Dependency> getRoleBinding();

    Collaboration getType();
}
