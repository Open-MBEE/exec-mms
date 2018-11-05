package org.openmbee.spec.uml;

public interface DurationConstraint extends IntervalConstraint, MofObject {

    Boolean isFirstEvent();

    DurationInterval getSpecification();
}
