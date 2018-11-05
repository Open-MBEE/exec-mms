package org.openmbee.spec.uml;

public interface ChangeEvent extends Event, MofObject {

    ValueSpecification getChangeExpression();
}
