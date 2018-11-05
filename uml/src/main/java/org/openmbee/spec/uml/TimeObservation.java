package org.openmbee.spec.uml;

public interface TimeObservation extends Observation, MofObject {

    NamedElement getEvent();

    Boolean isFirstEvent();
}
