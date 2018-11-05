package org.openmbee.spec.uml;

public interface Variable extends ConnectableElement, MultiplicityElement, MofObject {

    Activity getActivityScope();

    StructuredActivityNode getScope();
}
