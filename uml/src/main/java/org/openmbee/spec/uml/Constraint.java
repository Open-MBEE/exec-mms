package org.openmbee.spec.uml;

import java.util.List;

public interface Constraint extends PackageableElement, MofObject {

    List<Element> getConstrainedElement();

    Namespace getContext();

    ValueSpecification getSpecification();
}
