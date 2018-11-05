package org.openmbee.spec.uml;

public interface RemoveVariableValueAction extends WriteVariableAction, MofObject {

    Boolean isRemoveDuplicates();

    InputPin getRemoveAt();
}
