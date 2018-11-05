package org.openmbee.spec.uml;

public interface GeneralOrdering extends NamedElement, MofObject {

    OccurrenceSpecification getAfter();

    OccurrenceSpecification getBefore();
}
