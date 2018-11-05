package org.openmbee.spec.uml;

public interface ReduceAction extends Action, MofObject {

    InputPin getCollection();

    Boolean isOrdered();

    Behavior getReducer();

    OutputPin getResult();
}
