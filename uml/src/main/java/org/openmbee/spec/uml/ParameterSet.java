package org.openmbee.spec.uml;

import java.util.Collection;

public interface ParameterSet extends NamedElement, MofObject {

    Collection<Constraint> getCondition();

    Collection<Parameter> getParameter();
}
