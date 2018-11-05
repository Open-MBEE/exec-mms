package org.openmbee.spec.uml;

public interface ExecutionSpecification extends InteractionFragment, MofObject {

    OccurrenceSpecification getFinish();

    OccurrenceSpecification getStart();
}
