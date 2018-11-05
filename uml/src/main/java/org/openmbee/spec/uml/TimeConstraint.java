package org.openmbee.spec.uml;

public interface TimeConstraint extends IntervalConstraint, MofObject {

    Boolean isFirstEvent();

    TimeInterval getSpecification();
}
