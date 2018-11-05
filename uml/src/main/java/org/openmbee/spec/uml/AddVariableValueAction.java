package org.openmbee.spec.uml;

public interface AddVariableValueAction extends WriteVariableAction, MofObject {

    InputPin getInsertAt();

    Boolean isReplaceAll();
}
