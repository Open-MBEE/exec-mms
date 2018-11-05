package org.openmbee.spec.uml;

public interface CallOperationAction extends CallAction, MofObject {

    Operation getOperation();

    InputPin getTarget();
}
