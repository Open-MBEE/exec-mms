package org.openmbee.spec.uml;

public interface DurationObservation extends Observation, MofObject {

    NamedElement getEvent();

    Boolean isFirstEvent();
}
