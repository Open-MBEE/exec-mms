package org.openmbee.spec.uml;

public interface Interval extends ValueSpecification, MofObject {

    ValueSpecification getMax();

    ValueSpecification getMin();
}
