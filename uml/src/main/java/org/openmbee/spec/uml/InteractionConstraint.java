package org.openmbee.spec.uml;

public interface InteractionConstraint extends Constraint, MofObject {

    ValueSpecification getMaxint();

    ValueSpecification getMinint();
}
