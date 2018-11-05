package org.openmbee.spec.uml;

import java.util.Collection;

public interface TimeExpression extends ValueSpecification, MofObject {

    ValueSpecification getExpr();

    Collection<Observation> getObservation();
}
