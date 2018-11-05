package org.openmbee.spec.uml;

public interface ValueSpecificationAction extends Action, MofObject {

    OutputPin getResult();

    ValueSpecification getValue();
}
