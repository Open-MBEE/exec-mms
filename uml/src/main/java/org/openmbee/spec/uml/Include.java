package org.openmbee.spec.uml;

public interface Include extends DirectedRelationship, NamedElement, MofObject {

    UseCase getAddition();

    UseCase getIncludingCase();
}
