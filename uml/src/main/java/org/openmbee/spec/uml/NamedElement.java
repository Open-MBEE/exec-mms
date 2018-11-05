package org.openmbee.spec.uml;

import java.util.Collection;

public interface NamedElement extends Element, MofObject {

    Collection<Dependency> getClientDependency();

    String getName();

    StringExpression getNameExpression();

    Namespace getNamespace();

    String getQualifiedName();

    VisibilityKind getVisibility();
}
